package org.orcid.core.manager.impl;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.BiographyManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.read_only.impl.ProfileEntityManagerReadOnlyImpl;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Declan Newman (declan) Date: 10/02/2012
 */
public class ProfileEntityManagerImpl extends ProfileEntityManagerReadOnlyImpl implements ProfileEntityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEntityManagerImpl.class);

    @Resource
    private AffiliationsManager affiliationsManager;

    @Resource
    private ProfileFundingManager fundingManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private AddressManager addressManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileKeywordManager profileKeywordManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private EmailManager emailManager;

    @Resource
    private OtherNameManager otherNamesManager;

    @Resource
    private BiographyManager biographyManager;

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private RecordNameManager recordNameManager;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Override
    public boolean orcidExists(String orcid) {
        return profileDao.orcidExists(orcid);
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        return profileDao.hasBeenGivenPermissionTo(giverOrcid, receiverOrcid);
    }

    @Override
    public boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId) {
        return profileDao.existsAndNotClaimedAndBelongsTo(messageOrcid, clientId);
    }

    @Override
    public String findByCreditName(String creditName) {
        Name name = recordNameManager.findByCreditName(creditName);
        if (name == null) {
            return null;
        }
        return name.getPath();
    }

    /**
     * Enable developer tools on the given record
     * 
     * @param orcid
     *            record id
     * @return true if the developer tools where enabled on the given record
     */
    @Override
    public boolean enableDeveloperTools(String orcid) {
        return profileDao.updateDeveloperTools(orcid, true);
    }

    /**
     * Disable developer tools in the given record
     * 
     * @param orcid
     *            record id
     * @return true if the developer tools where disabled on the given record
     */
    @Override
    public boolean disableDeveloperTools(String orcid) {
        return profileDao.updateDeveloperTools(orcid, false);
    }

    @Override
    public boolean isProfileClaimed(String orcid) {
        return profileDao.getClaimedStatus(orcid);
    }

    /**
     * Get the group type of a profile
     * 
     * @param orcid
     *            The profile to look for
     * @return the group type, null if it is not a client
     */
    @Override
    public MemberType getGroupType(String orcid) {
        String groupType = profileDao.getGroupType(orcid);
        return MemberType.valueOf(groupType);
    }

    /**
     * Updates the DB and the cached value in the request scope.
     * 
     */
    @Override
    public void updateLastModifed(String orcid) {
        profileLastModifiedAspect.updateLastModifiedDateAndIndexingStatus(orcid);
    }

    @Override
    public boolean isDeactivated(String orcid) {
        return profileDao.isDeactivated(orcid);
    }

    @Override
    public boolean reviewProfile(String orcid) {
        return profileDao.reviewProfile(orcid);
    }

    @Override
    public boolean unreviewProfile(String orcid) {
        return profileDao.unreviewProfile(orcid);
    }

    @Override
    public void disableApplication(Long tokenId, String userOrcid) {
        orcidOauth2TokenService.disableAccessToken(tokenId, userOrcid);
    }

    @Override
    public List<ApplicationSummary> getApplications(String orcid) {
        List<OrcidOauth2TokenDetail> tokenDetails = orcidOauth2TokenService.findByUserName(orcid);
        List<ApplicationSummary> applications = new ArrayList<ApplicationSummary>();
        Map<Pair<String, Set<ScopePathType>>, ApplicationSummary> existingApplications = new HashMap<Pair<String, Set<ScopePathType>>, ApplicationSummary>();
        if (tokenDetails != null && !tokenDetails.isEmpty()) {
            for (OrcidOauth2TokenDetail token : tokenDetails) {
                if (token.getTokenDisabled() == null || !token.getTokenDisabled()) {
                    ClientDetailsEntity client = clientDetailsEntityCacheManager.retrieve(token.getClientDetailsId());
                    if (client != null) {
                        ApplicationSummary applicationSummary = new ApplicationSummary();
                        // Check the scopes
                        Set<ScopePathType> scopesGrantedToClient = ScopePathType.getScopesFromSpaceSeparatedString(token.getScope());
                        Map<ScopePathType, String> scopePathMap = new HashMap<ScopePathType, String>();
                        String scopeFullPath = ScopePathType.class.getName() + ".";
                        for (ScopePathType tempScope : scopesGrantedToClient) {
                            try {
                                scopePathMap.put(tempScope, localeManager.resolveMessage(scopeFullPath + tempScope.toString()));
                            } catch (NoSuchMessageException e) {
                                LOGGER.warn("No message to display for scope " + tempScope.toString());
                            }
                        }
                        // If there is at least one scope in this token, fill
                        // the application summary element
                        if (!scopePathMap.isEmpty()) {
                            applicationSummary.setScopePaths(scopePathMap);
                            applicationSummary.setOrcidHost(orcidUrlManager.getBaseHost());
                            applicationSummary.setOrcidUri(orcidUrlManager.getBaseUriHttp() + "/" + client.getId());
                            applicationSummary.setOrcidPath(client.getId());
                            applicationSummary.setName(client.getClientName());
                            applicationSummary.setWebsiteValue(client.getClientWebsite());
                            applicationSummary.setApprovalDate(token.getDateCreated());
                            applicationSummary.setTokenId(String.valueOf(token.getId()));
                            // Add member information
                            if (!PojoUtil.isEmpty(client.getGroupProfileId())) {
                                ProfileEntity member = profileEntityCacheManager.retrieve(client.getGroupProfileId());
                                applicationSummary.setGroupOrcidPath(member.getId());
                                applicationSummary.setGroupName(getMemberDisplayName(member));
                            }

                            if (shouldBeAddedToTheApplicationsList(applicationSummary, scopesGrantedToClient, existingApplications)) {
                                applications.add(applicationSummary);
                            }
                        }
                    }
                }
            }
        }

        return applications;
    }

    private boolean shouldBeAddedToTheApplicationsList(ApplicationSummary application, Set<ScopePathType> scopes,
            Map<Pair<String, Set<ScopePathType>>, ApplicationSummary> existingApplications) {
        boolean result = false;
        Pair<String, Set<ScopePathType>> key = Pair.of(application.getOrcidPath(), scopes);
        if (!existingApplications.containsKey(key)) {
            result = true;
        } else {
            Date existingAppCreatedDate = existingApplications.get(key).getApprovalDate();

            // This case should never happen
            if (existingAppCreatedDate == null) {
                result = true;
            }

            if (application.getApprovalDate().before(existingAppCreatedDate)) {
                result = true;
            }
        }

        if (result) {
            existingApplications.put(key, application);
        }
        return result;
    }

    private String getMemberDisplayName(ProfileEntity member) {
        RecordNameEntity recordName = member.getRecordNameEntity();
        if (recordName == null) {
            return StringUtils.EMPTY;
        }

        // If it is a member, return the credit name
        if (OrcidType.GROUP.equals(member.getOrcidType())) {
            return recordName.getCreditName();
        }

        Visibility namesVisibilty = Visibility.valueOf(recordName.getVisibility());
        if (Visibility.PUBLIC.equals(namesVisibilty)) {
            if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                String displayName = recordName.getGivenNames();
                String familyName = recordName.getFamilyName();
                if (StringUtils.isNotBlank(familyName)) {
                    displayName += " " + familyName;
                }
                return displayName;
            }
        }

        return StringUtils.EMPTY;
    }

    @Override
    public String getOrcidHash(String orcid) throws NoSuchAlgorithmException {
        if (PojoUtil.isEmpty(orcid)) {
            return null;
        }
        return encryptionManager.sha256Hash(orcid);
    }

    @Override
    public String retrivePublicDisplayName(String orcid) {
        String publicName = "";
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile != null) {
            RecordNameEntity recordName = profile.getRecordNameEntity();
            if (recordName != null) {
                Visibility namesVisibility = (recordName.getVisibility() != null) ? Visibility.valueOf(recordName.getVisibility())
                        : Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
                if (Visibility.PUBLIC.equals(namesVisibility)) {
                    if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                        publicName = recordName.getCreditName();
                    } else {
                        publicName = PojoUtil.isEmpty(recordName.getGivenNames()) ? "" : recordName.getGivenNames();
                        publicName += PojoUtil.isEmpty(recordName.getFamilyName()) ? "" : " " + recordName.getFamilyName();
                    }
                }
            }
        }
        return publicName;
    }

    @Override
    @Transactional
    public boolean claimProfileAndUpdatePreferences(String orcid, String email, Locale locale, Claim claim) {
        // Verify the email
        boolean emailVerified = emailManager.verifySetCurrentAndPrimary(orcid, email);
        if (!emailVerified) {
            throw new InvalidParameterException("Unable to claim and verify email: " + email + " for user: " + orcid);
        }

        // Update the profile entity fields
        ProfileEntity profile = profileDao.find(orcid);
        profile.setLastModified(new Date());
        profile.setIndexingStatus(IndexingStatus.REINDEX);
        profile.setClaimed(true);
        profile.setCompletedDate(new Date());
        if (locale != null) {
            profile.setLocale(locale.name());
        }
        if (claim != null) {
            profile.setActivitiesVisibilityDefault(claim.getActivitiesVisibilityDefault().getVisibility().name());
        }

        // Update the visibility for every bio element to the visibility
        // selected by the user
        // Update the bio
        org.orcid.jaxb.model.common_v2.Visibility defaultVisibility = org.orcid.jaxb.model.common_v2.Visibility
                .fromValue(claim.getActivitiesVisibilityDefault().getVisibility().value());
        if (profile.getBiographyEntity() != null) {
            profile.getBiographyEntity().setVisibility(defaultVisibility.name());
        }
        // Update address
        if (profile.getAddresses() != null) {
            for (AddressEntity a : profile.getAddresses()) {
                a.setVisibility(defaultVisibility.name());
            }
        }

        // Update the keywords
        if (profile.getKeywords() != null) {
            for (ProfileKeywordEntity k : profile.getKeywords()) {
                k.setVisibility(defaultVisibility.name());
            }
        }

        // Update the other names
        if (profile.getOtherNames() != null) {
            for (OtherNameEntity o : profile.getOtherNames()) {
                o.setVisibility(defaultVisibility.name());
            }
        }

        // Update the researcher urls
        if (profile.getResearcherUrls() != null) {
            for (ResearcherUrlEntity r : profile.getResearcherUrls()) {
                r.setVisibility(defaultVisibility.name());
            }
        }

        // Update the external identifiers
        if (profile.getExternalIdentifiers() != null) {
            for (ExternalIdentifierEntity e : profile.getExternalIdentifiers()) {
                e.setVisibility(defaultVisibility.name());
            }
        }
        profileDao.merge(profile);
        profileDao.flush();
        return true;
    }    

    @Override
    public void updateLocale(String orcid, Locale locale) {
        profileDao.updateLocale(orcid, locale.name());
    }

    @Override
    public boolean isProfileClaimedByEmail(String email) {
        return profileDao.getClaimedStatusByEmailHash(encryptionManager.getEmailHash(email));        
    }

    @Override
    public void updatePassword(String orcid, String password) {
        String encryptedPassword = encryptionManager.hashForInternalUse(password);
        profileDao.updateEncryptedPassword(orcid, encryptedPassword);
    }

    @Override
    public boolean isProfileDeprecated(String orcid) {
        return profileDao.isProfileDeprecated(orcid);
    }

    @Override
    public void updateLastLoginDetails(String orcid, String ipAddress) {
        profileDao.updateLastLoginDetails(orcid, ipAddress);
    }

    @Override
    public Locale retrieveLocale(String orcid) {
        String locale = profileDao.retrieveLocale(orcid);
        return Locale.valueOf(locale);
    }

    /**
     * Set the locked status of an account to true
     * 
     * @param orcid
     *            the id of the profile that should be locked
     * @return true if the account was locked
     */
    @Override
    public boolean lockProfile(String orcid, String lockReason, String description) {
        boolean wasLocked = profileDao.lockProfile(orcid, lockReason, description);
        if (wasLocked) {
            notificationManager.sendOrcidLockedEmail(orcid);
        }
        return wasLocked;
    }

    /**
     * Set the locked status of an account to false
     * 
     * @param orcid
     *            the id of the profile that should be unlocked
     * @return true if the account was unlocked
     */
    @Override
    public boolean unlockProfile(String orcid) {
        return profileDao.unlockProfile(orcid);
    }

    @Override
    public Date getLastLogin(String orcid) {
        return profileDao.getLastLogin(orcid);
    }

    @Override
    public void disable2FA(String orcid) {
        profileDao.disable2FA(orcid);
    }

    @Override
    public void enable2FA(String orcid) {
        profileDao.enable2FA(orcid);
    }

    @Override
    public void update2FASecret(String orcid, String secret) {
        profileDao.update2FASecret(orcid, secret);
    }
    
    /**
     * Clears all record info but the email addresses, that stay unmodified
     * */
    private void clearRecord(String orcid, Boolean disableTokens) {
        // Remove works
        workManager.removeAllWorks(orcid);

        // Remove funding
        fundingManager.removeAllFunding(orcid);
        
        // Remove affiliations
        affiliationsManager.removeAllAffiliations(orcid);
        
        // Remove peer reviews
        peerReviewManager.removeAllPeerReviews(orcid);
        
        // Remove addresses
        addressManager.removeAllAddress(orcid);
        
        // Remove external identifiers
        externalIdentifierManager.removeAllExternalIdentifiers(orcid);
        
        // Remove researcher urls
        researcherUrlManager.removeAllResearcherUrls(orcid);
        
        // Remove other names
        otherNamesManager.removeAllOtherNames(orcid);
        
        // Remove keywords
        profileKeywordManager.removeAllKeywords(orcid);
        
        // Remove biography
        if (biographyManager.exists(orcid)) {
            Biography deprecatedBio = new Biography();
            deprecatedBio.setContent(null);
            deprecatedBio.setVisibility(Visibility.PRIVATE);
            biographyManager.updateBiography(orcid, deprecatedBio);
        }
        
        // Set the deactivated names
        if (recordNameManager.exists(orcid)) {
            Name name = new Name();
            name.setCreditName(new CreditName());
            name.setGivenNames(new GivenNames("Given Names Deactivated"));
            name.setFamilyName(new FamilyName("Family Name Deactivated"));
            name.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
            name.setPath(orcid);
            recordNameManager.updateRecordName(orcid, name);
        }
        
        // 
        userConnectionDao.deleteByOrcid(orcid);
        
        if(disableTokens) {
            // Disable any token that belongs to this record
            orcidOauth2TokenDetailService.disableAccessTokenByUserOrcid(orcid, RevokeReason.RECORD_DEACTIVATED);
        }
        // Change default visibility to private
        profileDao.updateDefaultVisibility(orcid, Visibility.PRIVATE.name());
    }
}