package com.data.bean;

import java.io.Serializable;

public class SimpleCode implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8261892388142623567L;

	private Integer id;
	// 单品名称
    private String simpleName;
    // 条码
    private String barCode;
    // 步步高
    private String bbg;
    // 人人乐
    private String rrl;
    // 家润多
    private String jrd;
    // 易初
    private String yc;
    // 湖南天虹
    private String hnth;
    // 欧尚
    private String os;
    // 宾隆
    private String bl;
    // 北京华联
    private String bjhl;
    // 武商
    private String ws;
    // 冠超市
    private String gcs;
    // 欧亚车百
    private String oycb;
    // 佳惠
    private String jh;
    // 大润发
    private String drf;
    // 沃尔玛
    private String wem;
    // 中百
    private String zb;
    // 重百
    private String cb;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName == null ? null : simpleName.trim();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    public String getBbg() {
        return bbg;
    }

    public void setBbg(String bbg) {
        this.bbg = bbg == null ? null : bbg.trim();
    }

    public String getRrl() {
        return rrl;
    }

    public void setRrl(String rrl) {
        this.rrl = rrl == null ? null : rrl.trim();
    }

    public String getJrd() {
        return jrd;
    }

    public void setJrd(String jrd) {
        this.jrd = jrd == null ? null : jrd.trim();
    }

    public String getYc() {
        return yc;
    }

    public void setYc(String yc) {
        this.yc = yc == null ? null : yc.trim();
    }

    public String getHnth() {
        return hnth;
    }

    public void setHnth(String hnth) {
        this.hnth = hnth == null ? null : hnth.trim();
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os == null ? null : os.trim();
    }

    public String getBl() {
        return bl;
    }

    public void setBl(String bl) {
        this.bl = bl == null ? null : bl.trim();
    }

    public String getBjhl() {
        return bjhl;
    }

    public void setBjhl(String bjhl) {
        this.bjhl = bjhl == null ? null : bjhl.trim();
    }

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws == null ? null : ws.trim();
    }

    public String getGcs() {
        return gcs;
    }

    public void setGcs(String gcs) {
        this.gcs = gcs == null ? null : gcs.trim();
    }

    public String getOycb() {
        return oycb;
    }

    public void setOycb(String oycb) {
        this.oycb = oycb == null ? null : oycb.trim();
    }

    public String getJh() {
        return jh;
    }

    public void setJh(String jh) {
        this.jh = jh == null ? null : jh.trim();
    }

    public String getDrf() {
        return drf;
    }

    public void setDrf(String drf) {
        this.drf = drf == null ? null : drf.trim();
    }

    public String getWem() {
        return wem;
    }

    public void setWem(String wem) {
        this.wem = wem == null ? null : wem.trim();
    }

    public String getZb() {
        return zb;
    }

    public void setZb(String zb) {
        this.zb = zb == null ? null : zb.trim();
    }

    public String getCb() {
        return cb;
    }

    public void setCb(String cb) {
        this.cb = cb == null ? null : cb.trim();
    }
}