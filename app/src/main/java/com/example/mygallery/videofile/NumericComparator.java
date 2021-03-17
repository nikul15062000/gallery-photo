package com.example.mygallery.videofile;


public class NumericComparator {

   public static int s1_pos = 0;
    public static final String TAG = "NumericComparator";
    private static final int UNICODE_MAX = 1114111;

    public static int filevercmp(String s1, String s2) {
        String s1_suffix = "";
        String s2_suffix = "";
        int simple_cmp = strcmp(s1, s2);
        if (simple_cmp == 0) {
            return 0;
        }
        if (s1 == null || s1.length() == 0) {
            return -1;
        }
        if (s2 == null || s2.length() == 0) {
            return 1;
        }
        if (strcmp(".", s1) == 0) {
            return -1;
        }
        if (strcmp(".", s2) == 0) {
            return 1;
        }
        if (strcmp("..", s1) == 0) {
            return -1;
        }
        if (strcmp("..", s2) == 0) {
            return 1;
        }
        if (s1.codePointAt(0) == 46 && s2.codePointAt(0) != 46) {
            return -1;
        }
        if (s1.codePointAt(0) != 46 && s2.codePointAt(0) == 46) {
            return 1;
        }
        if (s1.codePointAt(0) == 46 && s2.codePointAt(0) == 46) {
            s1 = s1.substring(1, s1.length());
            s2 = s2.substring(1, s2.length());
        }
        s1_suffix = match_suffix(s1);
        s2_suffix = match_suffix(s2);
        int s1_len = s1.length() - s1_suffix.length();
        int s2_len = s2.length() - s2_suffix.length();
        if ((s1_suffix.length() > 0 || s2_suffix.length() > 0) && s1_len == s2_len && strncmp(s1, s2, s1_len) == 0) {
            s1_len = s1.length();
            s2_len = s2.length();
        }
        int result = verrevcmp(s1.substring(0, s1_len), s2.substring(0, s2_len));
        return result != 0 ? result : simple_cmp;
    }

    public static int verrevcmp(String s1, String s2)
    {
        int first_diff;
        int s2_pos = 0;

        do
            {
            if (s1_pos >= s1.length() && s2_pos >= s2.length())
            {
                return 0;
            }
            first_diff = 0;
            while (true)
            {
                if ((s1_pos >= s1.length() || c_isdigit(s1.codePointAt(s1_pos))) && (s2_pos >= s2.length() || c_isdigit(s2.codePointAt(s2_pos)))) {
                    while (s1_pos < s1.length() && s1.codePointAt(s1_pos) == 48) {
                        s1_pos++;
                    }
                } else {
                    int s1_c = s1_pos >= s1.length() ? 0 : order(s1.codePointAt(s1_pos));
                    int s2_c = s2_pos >= s2.length() ? 0 : order(s2.codePointAt(s2_pos));
                    if (s1_c != s2_c) {
                        return s1_c - s2_c;
                    }
                    s1_pos++;
                    s2_pos++;
                }
            }
            } while (first_diff == 0);
    }

    private static int order(int c) {
        if (c_isdigit(c)) {
            return 0;
        }
        if (c_isalpha(c)) {
            return c;
        }
        if (c == TransportMediator.KEYCODE_MEDIA_PLAY) {
            return -1;
        }
        return (UNICODE_MAX + c) + 1;
    }

    private static String match_suffix(String str) {
        String match = "";
        boolean read_alpha = false;
        while (str.length() > 0) {
            if (read_alpha) {
                read_alpha = false;
                if (!(c_isalpha(str.codePointAt(0)) || TransportMediator.KEYCODE_MEDIA_PLAY == str.codePointAt(0))) {
                    match = "";
                }
            } else if (46 == str.codePointAt(0)) {
                read_alpha = true;
                if (match.length() == 0) {
                    match = str;
                }
            } else if (!(c_isalnum(str.codePointAt(0)) || TransportMediator.KEYCODE_MEDIA_PLAY == str.codePointAt(0))) {
                match = "";
            }
            str = str.substring(1, str.length());
        }
        return match;
    }

    private static int strcmp(String s1, String s2) {
        return s1.compareTo(s2);
    }

    private static int strncmp(String s1, String s2, int len) {
        return s1.substring(0, Math.min(len, s1.length())).compareTo(s2.substring(0, Math.min(len, s2.length())));
    }

    private static boolean c_isdigit(int c) {
        return Character.isDigit(c);
    }

    private static boolean c_isalpha(int c) {
        return Character.isLetter(c);
    }

    private static boolean c_isalnum(int c) {
        return Character.isLetterOrDigit(c);
    }
}
