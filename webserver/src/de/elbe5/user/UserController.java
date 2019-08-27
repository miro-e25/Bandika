/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.application.MailHelper;
import de.elbe5.application.Statics;
import de.elbe5.base.cache.Strings;
import de.elbe5.configuration.Configuration;
import de.elbe5.page.PageCache;
import de.elbe5.page.PageData;
import de.elbe5.jsppage.JspPageData;
import de.elbe5.request.*;
import de.elbe5.rights.Right;
import de.elbe5.rights.RightsCache;
import de.elbe5.rights.SystemZone;
import de.elbe5.servlet.Controller;

import java.util.Locale;

public class UserController extends Controller {

    public static final String KEY = "user";

    private static UserController instance = new UserController();

    public static UserController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public IActionResult openLogin(RequestData rdata) {
        return showLogin();
    }

    public IActionResult login(RequestData rdata) {
        if (!rdata.isPostback()) {
            return methodNotAllowed();
        }
        String login = rdata.getString("login");
        String pwd = rdata.getString("password");
        if (login.length() == 0 || pwd.length() == 0) {
            rdata.setMessage(Strings.string("_notComplete",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        UserData data = UserBean.getInstance().loginUser(login, pwd);
        if (data == null) {
            rdata.setMessage(Strings.string("_badLogin",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return openLogin(rdata);
        }
        rdata.setSessionUser(data);
        data.checkRights();
        return showHome();
    }

    public IActionResult showCaptcha(RequestData rdata) {
        String captcha = (String) rdata.getSessionObject(Statics.KEY_CAPTCHA);
        if (captcha == null) {
            return notFound();
        }
        BinaryFile data = UserSecurity.getCaptcha(captcha);
        assert data != null;
        return new BinaryActionResult(data, false);
    }

    public IActionResult renewCaptcha(RequestData rdata) {
        rdata.setSessionObject(Statics.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
        return new HtmlActionResult("ok");
    }

    public IActionResult logout(RequestData rdata) {
        Locale locale = rdata.getSessionLocale();
        rdata.setSessionUser(null);
        rdata.resetSession();
        rdata.setMessage(Strings.string("_loggedOut",locale), Statics.MESSAGE_TYPE_SUCCESS);
        return showHome();
    }

    public IActionResult openRegistration(RequestData rdata) {
        rdata.put("userData", new UserData());
        rdata.setSessionObject(Statics.KEY_CAPTCHA, UserSecurity.generateCaptchaString());
        return showRegistration();
    }

    public IActionResult register(RequestData rdata) {
        UserData user = new UserData();
        rdata.put("userData", user);
        user.readRegistrationRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showRegistration();
        }
        if (UserBean.getInstance().doesLoginExist(user.getLogin())) {
            rdata.addFormField("login");
            rdata.addFormError(Strings.string("_loginExistsError",rdata.getSessionLocale()));
        }
        if (UserBean.getInstance().doesEmailExist(user.getEmail())) {
            rdata.addFormField("email");
            rdata.addFormError(Strings.string("_emailInUseError",rdata.getSessionLocale()));
        }
        String captchaString = rdata.getString("captcha");
        if (!captchaString.equals(rdata.getSessionObject(Statics.KEY_CAPTCHA))) {
            rdata.addFormField("captcha");
            rdata.addFormError(Strings.string("_captchaError",rdata.getSessionLocale()));
        }
        if (!rdata.hasFormError()) {
            return showRegistration();
        }
        user.setApproved(false);
        user.setApprovalCode(UserSecurity.getApprovalString());
        user.setId(UserBean.getInstance().getNextId());
        if (!UserBean.getInstance().saveUser(user)) {
            rdata.setMessage(Strings.string("_saveError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        Locale locale = rdata.getSessionLocale();
        String mailText = Strings.string("_registrationVerifyMail",locale) + " " + rdata.getSessionHost() + "/ctrl/user/verifyEmail/" + user.getId() + "?approvalCode=" + user.getApprovalCode();
        if (!MailHelper.sendPlainMail(user.getEmail(), Strings.string("_registrationRequest",locale), mailText)) {
            rdata.setMessage(Strings.string("_emailError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        return showRegistrationDone();
    }

    public IActionResult verifyEmail(RequestData rdata) {
        int userId = rdata.getId();
        String approvalCode = rdata.getString("approvalCode");
        UserData data = UserBean.getInstance().getUser(userId);
        if (approvalCode.isEmpty() || !approvalCode.equals(data.getApprovalCode())) {
            rdata.setMessage(Strings.string("_emailVerificationFailed",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showHome();
        }
        UserBean.getInstance().saveUserVerifyEmail(data);
        Locale locale = rdata.getSessionLocale();
        String mailText = Strings.string("_registrationRequestMail",locale) + " " + data.getName() + "(" + data.getId() + ")";
        if (!MailHelper.sendPlainMail(Configuration.getInstance().getMailReceiver(), Strings.string("_registrationRequest",locale), mailText)) {
            rdata.setMessage(Strings.string("_emailError",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return showRegistration();
        }
        return showEmailVerification();
    }

    public IActionResult openEditGroup(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        int groupId = rdata.getId();
        GroupData data = GroupBean.getInstance().getGroup(groupId);
        data.checkRights();
        rdata.setSessionObject("groupData", data);
        return showEditGroup();
    }

    public IActionResult openCreateGroup(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        GroupData data = new GroupData();
        data.setNew(true);
        data.setId(GroupBean.getInstance().getNextId());
        rdata.setSessionObject("groupData", data);
        return showEditGroup();
    }

    public IActionResult saveGroup(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        GroupData data = (GroupData) rdata.getSessionObject("groupData");
        if (data == null)
            return noData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditGroup();
        }
        GroupBean.getInstance().saveGroup(data);
        RightsCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_groupSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/admin/openSystemAdministration?groupId=" + data.getId());
    }

    public IActionResult deleteGroup(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        int id = rdata.getId();
        if (id < BaseIdData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
        }
        GroupBean.getInstance().deleteGroup(id);
        RightsCache.getInstance().setDirty();
        rdata.setMessage(Strings.string("_groupDeleted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
    }

    public IActionResult openEditUser(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        int userId = rdata.getId();
        UserData data = UserBean.getInstance().getUser(userId);
        rdata.setSessionObject("userData", data);
        return showEditUser();
    }

    public IActionResult openCreateUser(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        UserData data = new UserData();
        data.setNew(true);
        data.setId(UserBean.getInstance().getNextId());
        rdata.setSessionObject("userData", data);
        return showEditUser();
    }

    public IActionResult saveUser(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        UserData data = (UserData) rdata.getSessionObject("userData");
        if (data == null)
            return noData(rdata);
        data.readRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showEditUser();
        }
        UserBean.getInstance().saveUser(data);
        RightsCache.getInstance().setDirty();
        if (rdata.getUserId() == data.getId()) {
            data.checkRights();
            rdata.setSessionUser(data);
        }
        rdata.setMessage(Strings.string("_userSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/admin/openSystemAdministration?userId=" + data.getId());
    }

    public IActionResult deleteUser(RequestData rdata) {
        if (!rdata.hasSystemRight(SystemZone.USER, Right.EDIT))
            return forbidden(rdata);
        int id = rdata.getId();
        if (id < BaseIdData.ID_MIN) {
            rdata.setMessage(Strings.string("_notDeletable",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_ERROR);
            return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
        }
        UserBean.getInstance().deleteUser(id);
        rdata.setMessage(Strings.string("_userDeleted",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new ForwardActionResult("/ctrl/admin/openSystemAdministration");
    }

    public IActionResult showPortrait(RequestData rdata) {
        int userId = rdata.getId();
        BinaryFile file = UserBean.getInstance().getBinaryPortraitData(userId);
        if (file == null)
            return notFound();
        return new BinaryActionResult(file, false);
    }

    public IActionResult changeLocale(RequestData rdata) {
        String language = rdata.getString("language");
        Locale locale = new Locale(language);
        rdata.setSessionLocale(locale);
        PageData homePage = PageCache.getInstance().getHomePage(rdata.getSessionLocale());
        return new RedirectActionResult(homePage.getUrl());
    }

    public IActionResult openProfile(RequestData rdata) {
        if (!rdata.isLoggedIn())
            return forbidden(rdata);
        return showProfile();
    }

    public IActionResult openChangePassword(RequestData rdata) {
        if (!rdata.isLoggedIn())
            return forbidden(rdata);
        return showChangePassword();
    }

    public IActionResult changePassword(RequestData rdata) {
        if (!rdata.isLoggedIn() || rdata.getUserId() != rdata.getId())
            return forbidden(rdata);
        UserData user = UserBean.getInstance().getUser(rdata.getSessionUser().getId());
        if (user == null)
            return noData(rdata);
        String oldPassword = rdata.getString("oldPassword");
        String newPassword = rdata.getString("newPassword1");
        String newPassword2 = rdata.getString("newPassword2");
        Locale locale = rdata.getSessionLocale();
        if (newPassword.length() < UserData.MIN_PASSWORD_LENGTH) {
            rdata.addFormField("newPassword1");
            rdata.addFormError(Strings.string("_passwordLengthError",locale));
            return showChangePassword();
        }
        if (!newPassword.equals(newPassword2)) {
            rdata.addFormField("newPassword1");
            rdata.addFormField("newPassword2");
            rdata.addFormError(Strings.string("_passwordsDontMatch",locale));
            return showChangePassword();
        }
        UserData data = UserBean.getInstance().loginUser(user.getLogin(), oldPassword);
        if (data == null) {
            rdata.addFormField("newPassword1");
            rdata.addFormError(Strings.string("_badLogin",locale));
            return showChangePassword();
        }
        data.setPassword(newPassword);
        UserBean.getInstance().saveUserPassword(data);
        rdata.setMessage(Strings.string("_passwordChanged",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/user/openProfile");
    }

    public IActionResult openChangeProfile(RequestData rdata) {
        if (!rdata.isLoggedIn())
            return forbidden(rdata);
        return showChangeProfile();
    }

    public IActionResult changeProfile(RequestData rdata) {
        int userId = rdata.getId();
        if (!rdata.isLoggedIn() || rdata.getUserId() != userId)
            return forbidden(rdata);
        UserData data = UserBean.getInstance().getUser(userId);
        data.readProfileRequestData(rdata);
        if (!rdata.checkFormErrors()) {
            return showChangeProfile();
        }
        UserBean.getInstance().saveUserProfile(data);
        rdata.setSessionUser(data);
        rdata.setMessage(Strings.string("_userSaved",rdata.getSessionLocale()), Statics.MESSAGE_TYPE_SUCCESS);
        return new CloseDialogActionResult("/ctrl/user/openProfile");
    }

    protected IActionResult showLogin() {
        return new ForwardActionResult("/WEB-INF/_jsp/user/login.jsp");
    }

    protected IActionResult showEditGroup() {
        return new ForwardActionResult("/WEB-INF/_jsp/user/editGroup.ajax.jsp");
    }

    protected IActionResult showEditUser() {
        return new ForwardActionResult("/WEB-INF/_jsp/user/editUser.ajax.jsp");
    }

    protected IActionResult showProfile() {
        JspPageData pageData = new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/profile.jsp");
        return new PageActionResult(pageData);
    }

    protected IActionResult showChangePassword() {
        return new ForwardActionResult("/WEB-INF/_jsp/user/changePassword.ajax.jsp");
    }

    protected IActionResult showChangeProfile() {
        return new ForwardActionResult("/WEB-INF/_jsp/user/changeProfile.ajax.jsp");
    }

    protected IActionResult showRegistration() {
        JspPageData pageData = new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/registration.jsp");
        return new PageActionResult(pageData);
    }

    protected IActionResult showRegistrationDone() {
        JspPageData pageData = new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/registrationDone.jsp");
        return new PageActionResult(pageData);
    }

    protected IActionResult showEmailVerification() {
        JspPageData pageData = new JspPageData();
        pageData.setJsp("/WEB-INF/_jsp/user/verifyRegistrationEmail.jsp");
        return new PageActionResult(pageData);
    }

}
