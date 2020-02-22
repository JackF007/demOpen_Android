package com.gaicomo.app.utils;

import android.os.Environment;

import java.util.regex.Pattern;

/**
 * Created by kindlebit on 9/12/2016.
 */
public interface Constant {


    public   String APP_VERSION = "appversion";

    public   Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public  String IMAGE_PATH = "image_path";
    public  int PICK_CAMERA = 3;
    public  int PICK_GALLERY = 4;
    public  int RC_SIGN_IN = 9001;
    public  int SEARCH = 1001;


    public  String RESULT      = "Result";

    public  int PICK_IMAGE_ID = 100;

    public  String MESSAGES = "message";

    public  int NOTIFICATION_ID = 100001;
    public  int NOTIFICATION_ID_BIG_IMAGE = 1000001;


    public  String STATUS               ="status";
    public  String DATA                 ="data";
    public  String MESSAGE              ="message";

    public  String NAME                 ="name";
    public  String EMAIL                ="email";
    public  String Mobile               ="mobile";
    public  String ADDRESS              ="address";
    public  String Dob                  = "dob";
    public  String Country_Code         ="country_code";
    public  String Device_Type          ="device_type";
    public  String Device_Token         ="device_token";
    public  String Login_Type           ="login_type";
    public  String Password             ="password";
    public  String CODE                 ="code";
    public  String USER_ID              ="user_id";
    public  String ID                   ="id";
    public  String GENDER               ="gender";
    public  String description          ="description";


    public  String CATEGORY_ID          ="category_id";
    public  String ROOT_ID              ="root_id";
    public  String SUB_CAT_ID           ="sub_cat_id";
    public  String CHILD_ID             ="child_id";
    public  String LAST_POST_ID         ="last_post_id";
    public  String TWEET_ID             ="tweet_id";
    public  String POST_ID              ="post_id";
    public  String LAST_COMMENT_ID      ="last_comment_id";
    public  String COMMENT_ID           ="comment_id";
    public  String TAG_ID               ="tag_id";
    public  String LAST_COUNT_VALUE     ="last_count";
    public  String COMMENT              ="comment";
    public  String PHOTO                ="photo";
    public  String PROFILE_PICTURE      ="profile_picture";
    public  String POST                 ="post";
    public  String TAG                  ="tag";



    public  String Title                ="title";

    public  String TWEET                ="TWEET";
    public  String OPEN_DATA            ="open_data";
    public  String FOREIGN_INVESTMENT   ="foreign_investment";
    public  String COMPETITION          ="competition";

    public  String TYPE                 ="type";

    public  String IMAGE_DIRECTORY_NAME ="GiacomoDownloads";
    public  String FOLDER_NAME          = "FolderName";
    public  String FILE_NAME            = "FileName";
    public  String THUMBNAIL_NAME       = "ThumbName";
    public  String FILE_SIZE            = "FileSize";
    public  String FILES                = "Files";
    public  String MESSAGE_PROGRESS     = "message_progress";

    public  String SEARCH_TEXT          = "search_text";
    public  String SEARCH_kEY           = "search";
    public  String URL                  = "url";
}
