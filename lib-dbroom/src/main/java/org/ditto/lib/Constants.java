package org.ditto.lib;

import java.util.UUID;

public class Constants {
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxd930ea5d5a258f4f";
	public final static String TEST_ARTICLE_ID="TEST_ARTICLE_ID12345678";
	public static final String MYLOGIN = "conanchen";
	public static final String TEST_USER_ID = UUID.nameUUIDFromBytes("TEST_USER_ID0000000000001".getBytes()).toString();;
    public static final String ARTICLE_TYPE_BUYANSWER_UUID = UUID.nameUUIDFromBytes("ARTICLE_TYPE_BUYANSWER_UUID000001".getBytes()).toString();
	public static final String ARTICLE_TYPE_BUYREAD_UUID = UUID.nameUUIDFromBytes("ARTICLE_TYPE_BUYREAD_UUID000001".getBytes()).toString();
	public static final String ARTICLE_TYPE_SELLREAD_UUID = UUID.nameUUIDFromBytes("ARTICLE_TYPE_SELLREAD_UUID000001".getBytes()).toString();

    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}

}