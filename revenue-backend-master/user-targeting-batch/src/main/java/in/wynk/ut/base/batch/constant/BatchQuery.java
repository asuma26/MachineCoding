package in.wynk.ut.base.batch.constant;

public interface BatchQuery {

    String INSERT_UT_TARGET = "INSERT INTO user_targeting.ad_targeting (uid, adid, targeted) VALUES (?1, ?2, ?3)";

}
