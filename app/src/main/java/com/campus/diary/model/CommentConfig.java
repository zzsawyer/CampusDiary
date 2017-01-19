package com.campus.diary.model;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class CommentConfig {
    public static enum Type{
        PUBLIC("public"),
        REPLY("reply");

        private String value;
        private Type(String value){
            this.value = value;
        }

    }

    public String circleId;
    public int circlePosition;
    public int commentPosition;
    public Type commentType;
    public User replyUser;

    @Override
    public String toString() {
        String replyUserStr = "";
        if(replyUser != null){
            replyUserStr = replyUser.toString();
        }
        return "circlePosition = " + circlePosition
                + "; commentPosition = " + commentPosition
                + "; commentType ＝ " + commentType
                + "; replyUser = " + replyUserStr;
    }
}
