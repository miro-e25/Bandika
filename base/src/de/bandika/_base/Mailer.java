/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.application.Configuration;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;
import java.util.ArrayList;

/**
 * Class Mailer is for basic mailing tasks.<br>
 * Usage:
 */
public class Mailer extends BaseData {

  public final static String DATAKEY = "data|mailer";

  protected String from = null;
  protected String to = null;
  protected String cc = null;
  protected String bcc = null;
  protected String subject = null;
  protected String content = "";
  protected String contentType = "text/plain";
  protected ArrayList<FileData> files = null;
  protected String replyTo = null;
  protected String smtpHost = null;

  public void setFrom(String from) {
    this.from = from;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public void setBcc(String bcc) {
    this.bcc = bcc;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setText(String str) {
    this.content = str;
    this.contentType = "text/plain";
  }

  public void setHtml(String str) {
    this.content = str;
    this.contentType = "text/html";
  }

  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  public void addFile(FileData data) {
    if (files == null)
      files = new ArrayList<FileData>();
    files.add(data);
  }

  public boolean isComplete() {
    return DataHelper.isComplete(smtpHost)
      && DataHelper.isComplete(from)
      && DataHelper.isComplete(to)
      && DataHelper.isComplete(subject)
      && DataHelper.isComplete(content);
  }

  public MimeMessage createMessage(Session session) throws Exception {
    MimeMessage msg = new MimeMessage(session);
    if (from == null)
      from = Configuration.getConfigs().get("mailSender");
    if (from != null)
      msg.setFrom(new InternetAddress(from));
    else
      msg.setFrom();
    if (replyTo == null)
      replyTo = from;
    if (replyTo != null) {
      msg.setReplyTo(InternetAddress.parse(replyTo, false));
    }
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
    if (cc != null)
      msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
    if (bcc != null)
      msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
    if (subject != null)
      msg.setSubject(subject);
    else
      msg.setSubject("");
    if (files != null) {
      MimeMultipart mp = new MimeMultipart();
      MimeBodyPart mbpText = new MimeBodyPart();
      mbpText.setContent(content, contentType);
      mp.addBodyPart(mbpText);
      for (FileData file : files) {
        InternetHeaders headers = new InternetHeaders();
        headers.addHeader("Content-type", file.getContentType());
        headers.addHeader("Content-disposition", "attachment; filename=" + file.getFileName());
        MimeBodyPart mbpFile = new MimeBodyPart(headers, file.getBytes());
        mp.addBodyPart(mbpFile);
      }
      msg.setContent(mp);
    } else {
      msg.setContent(content, contentType);
    }
    msg.setSentDate(new Date());
    return msg;
  }

  public boolean sendMail() throws Exception {
    if (to == null)
      return false;
    Properties props = System.getProperties();
    if (smtpHost == null)
      smtpHost = Configuration.getConfigs().get("mailHost");
    if (smtpHost != null)
      props.put("mail.smtp.host", smtpHost);
    Session session = Session.getInstance(props, null);
    MimeMessage msg = createMessage(session);
    Transport.send(msg);
    return true;
  }

  public boolean sendMail(String username, String password) throws Exception {
    if (to == null)
      return false;
    Properties props = System.getProperties();
    if (smtpHost == null)
      smtpHost = Configuration.getConfigs().get("mailHost");
    if (smtpHost != null)
      props.put("mail.smtp.host", smtpHost);
    Session session = Session.getInstance(props, null);
    MimeMessage msg = createMessage(session);
    Transport transport = session.getTransport("smtp");
    transport.connect(smtpHost, username, password);
    transport.sendMessage(msg, msg.getAllRecipients());
    transport.close();
    return true;
  }

}