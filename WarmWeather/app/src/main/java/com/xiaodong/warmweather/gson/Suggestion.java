package com.xiaodong.warmweather.gson;

/**
 * Created by Administrator on 2017/2/20 0020.
 */

public class Suggestion {

    /**
     * air : {"brf":"良","txt":"气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。"}
     * comf : {"brf":"很不舒适","txt":"白天天气虽然晴好，但天气凉，您会感觉很冷，不舒适，请注意保暖防寒。"}
     * cw : {"brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"}
     * drsg : {"brf":"寒冷","txt":"天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。"}
     * flu : {"brf":"极易发","txt":"天气寒冷，且昼夜温差很大，极易发生感冒。请特别注意增加衣服保暖防寒。"}
     * sport : {"brf":"较不宜","txt":"天气较好，但考虑风力较大，天气寒冷，推荐您进行室内运动，若在户外运动须注意保暖。"}
     * trav : {"brf":"一般","txt":"天空状况还是比较好的，但温度很低，且风稍大，会让您感觉很冷，外出可选择雪上项目，并注意防寒保暖。"}
     * uv : {"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}
     */

    private AirBean air;
    private ComfBean comf;
    private CwBean cw;
    private DrsgBean drsg;
    private FluBean flu;
    private SportBean sport;
    private TravBean trav;
    private UvBean uv;

    public AirBean getAir() {
        return air;
    }

    public void setAir(AirBean air) {
        this.air = air;
    }

    public ComfBean getComf() {
        return comf;
    }

    public void setComf(ComfBean comf) {
        this.comf = comf;
    }

    public CwBean getCw() {
        return cw;
    }

    public void setCw(CwBean cw) {
        this.cw = cw;
    }

    public DrsgBean getDrsg() {
        return drsg;
    }

    public void setDrsg(DrsgBean drsg) {
        this.drsg = drsg;
    }

    public FluBean getFlu() {
        return flu;
    }

    public void setFlu(FluBean flu) {
        this.flu = flu;
    }

    public SportBean getSport() {
        return sport;
    }

    public void setSport(SportBean sport) {
        this.sport = sport;
    }

    public TravBean getTrav() {
        return trav;
    }

    public void setTrav(TravBean trav) {
        this.trav = trav;
    }

    public UvBean getUv() {
        return uv;
    }

    public void setUv(UvBean uv) {
        this.uv = uv;
    }

    public static class AirBean {
        /**
         * brf : 良
         * txt : 气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class ComfBean {
        /**
         * brf : 很不舒适
         * txt : 白天天气虽然晴好，但天气凉，您会感觉很冷，不舒适，请注意保暖防寒。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class CwBean {
        /**
         * brf : 较适宜
         * txt : 较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class DrsgBean {
        /**
         * brf : 寒冷
         * txt : 天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class FluBean {
        /**
         * brf : 极易发
         * txt : 天气寒冷，且昼夜温差很大，极易发生感冒。请特别注意增加衣服保暖防寒。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class SportBean {
        /**
         * brf : 较不宜
         * txt : 天气较好，但考虑风力较大，天气寒冷，推荐您进行室内运动，若在户外运动须注意保暖。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class TravBean {
        /**
         * brf : 一般
         * txt : 天空状况还是比较好的，但温度很低，且风稍大，会让您感觉很冷，外出可选择雪上项目，并注意防寒保暖。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class UvBean {
        /**
         * brf : 最弱
         * txt : 属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。
         */

        private String brf;
        private String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }
}
