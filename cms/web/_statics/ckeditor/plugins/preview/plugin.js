﻿/*
 Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.

 For licensing, see LICENSE.md or http://ckeditor.com/license

*/
(function(){var h,k={modes:{wysiwyg:1,source:1},canUndo:!1,readOnly:1,exec:function(a){var g,b=a.config,f=b.baseHref?'\x3cbase href\x3d"'+b.baseHref+'"/\x3e':"";if(b.fullPage)g=a.getData().replace(/<head>/,"$\x26"+f).replace(/[^>]*(?=<\/title>)/,"$\x26 \x26mdash; "+a.lang.preview.preview);else{var b="\x3cbody ",d=a.document&&a.document.getBody();d&&(d.getAttribute("id")&&(b+='id\x3d"'+d.getAttribute("id")+'" '),d.getAttribute("class")&&(b+='class\x3d"'+d.getAttribute("class")+'" '));b+="\x3e";g=a.config.docType+
'\x3chtml dir\x3d"'+a.config.contentsLangDirection+'"\x3e\x3chead\x3e'+f+"\x3ctitle\x3e"+a.lang.preview.preview+"\x3c/title\x3e"+CKEDITOR.tools.buildStyleHtml(a.config.contentsCss)+"\x3c/head\x3e"+b+a.getData()+"\x3c/body\x3e\x3c/html\x3e"}f=640;b=420;d=80;try{var c=window.screen,f=Math.round(.8*c.width),b=Math.round(.7*c.height),d=Math.round(.1*c.width)}catch(k){}if(!1===a.fire("contentPreview",a={dataValue:g}))return!1;var c="",e;CKEDITOR.env.ie&&(window._cke_htmlToLoad=a.dataValue,e="javascript:void( (function(){document.open();"+
("("+CKEDITOR.tools.fixDomain+")();").replace(/\/\/.*?\n/g,"").replace(/parent\./g,"window.opener.")+"document.write( window.opener._cke_htmlToLoad );document.close();window.opener._cke_htmlToLoad \x3d null;})() )",c="");CKEDITOR.env.gecko&&(window._cke_htmlToLoad=a.dataValue,c=CKEDITOR.getUrl(h+"preview.html"));c=window.open(c,null,"toolbar\x3dyes,location\x3dno,status\x3dyes,menubar\x3dyes,scrollbars\x3dyes,resizable\x3dyes,width\x3d"+f+",height\x3d"+b+",left\x3d"+d);CKEDITOR.env.ie&&c&&(c.location=
e);CKEDITOR.env.ie||CKEDITOR.env.gecko||(e=c.document,e.open(),e.write(a.dataValue),e.close());return!0}};CKEDITOR.plugins.add("preview",{lang:"de,en",icons:"preview,preview-rtl",hidpi:!0,init:function(a){a.elementMode!=CKEDITOR.ELEMENT_MODE_INLINE&&(h=this.path,a.addCommand("preview",k),a.ui.addButton&&a.ui.addButton("Preview",{label:a.lang.preview.preview,command:"preview",toolbar:"document,40"}))}})})();