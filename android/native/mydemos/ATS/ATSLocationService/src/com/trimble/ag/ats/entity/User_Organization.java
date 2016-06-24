package com.trimble.ag.ats.entity;

import com.trimble.ag.ats.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.trimble.ag.ats.dao.OrganizationDao;
import com.trimble.ag.ats.dao.UserDao;
import com.trimble.ag.ats.dao.User_OrganizationDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "USER__ORGANIZATION".
 */
public class User_Organization {

    private Long id;
    private Integer serverMode;
    /** Not-null value. */
    private String orgId;
    private long userId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient User_OrganizationDao myDao;

    private Organization organization;
    private String organization__resolvedKey;

    private User user;
    private Long user__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public User_Organization() {
    }

    public User_Organization(Long id) {
        this.id = id;
    }

    public User_Organization(Long id, Integer serverMode, String orgId, long userId) {
        this.id = id;
        this.serverMode = serverMode;
        this.orgId = orgId;
        this.userId = userId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUser_OrganizationDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getServerMode() {
        return serverMode;
    }

    public void setServerMode(Integer serverMode) {
        this.serverMode = serverMode;
    }

    /** Not-null value. */
    public String getOrgId() {
        return orgId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** To-one relationship, resolved on first access. */
    public Organization getOrganization() {
        String __key = this.orgId;
        if (organization__resolvedKey == null || organization__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            OrganizationDao targetDao = daoSession.getOrganizationDao();
            Organization organizationNew = targetDao.load(__key);
            synchronized (this) {
                organization = organizationNew;
            	organization__resolvedKey = __key;
            }
        }
        return organization;
    }

    public void setOrganization(Organization organization) {
        if (organization == null) {
            throw new DaoException("To-one property 'orgId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.organization = organization;
            orgId = organization.getOrgId();
            organization__resolvedKey = orgId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        long __key = this.userId;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            userId = user.getUserId();
            user__resolvedKey = userId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}