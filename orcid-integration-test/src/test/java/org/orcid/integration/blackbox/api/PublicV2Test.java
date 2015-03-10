/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.publicV2.PublicV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.error.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingExternalIdentifier;
import org.orcid.jaxb.model.record.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.FundingGroup;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.WorkGroup;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Monenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PublicV2Test {
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource
    private PublicV2ApiClientImpl publicV2ApiClient;

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private OauthHelper oauthHelper;

    static String accessToken = null;

    @After
    public void before() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }

    @After
    public void after() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }

    /**
     * VIEW PUBLIC INFO
     * */
    @Test
    public void testViewWorkAndWorkSummary() throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        ClientResponse getWorkResponse = publicV2ApiClient.viewWorkXml(user1OrcidId, putCode);
        assertNotNull(getWorkResponse);
        Work work = getWorkResponse.getEntity(Work.class);
        assertNotNull(work);
        assertEquals("common:title", work.getWorkTitle().getTitle().getContent());

        ClientResponse getWorkSummaryResponse = publicV2ApiClient.viewWorkSummaryXml(user1OrcidId, putCode);
        assertNotNull(getWorkSummaryResponse);
        WorkSummary summary = getWorkSummaryResponse.getEntity(WorkSummary.class);
        assertNotNull(summary);
        assertEquals("common:title", summary.getTitle().getTitle().getContent());
    }

    @Test
    public void testViewFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);
        fundingToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, fundingToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getFundingResponse = publicV2ApiClient.viewFundingXml(user1OrcidId, putCode);
        assertNotNull(getFundingResponse);
        Funding funding = getFundingResponse.getEntity(Funding.class);
        assertNotNull(funding);
        assertEquals("common:title", funding.getTitle().getTitle().getContent());

        ClientResponse getFundingSummaryResponse = publicV2ApiClient.viewFundingSummaryXml(user1OrcidId, putCode);
        assertNotNull(getFundingSummaryResponse);
        FundingSummary summary = getFundingSummaryResponse.getEntity(FundingSummary.class);
        assertNotNull(summary);
        assertEquals("common:title", summary.getTitle().getTitle().getContent());
    }

    @Test
    public void testViewEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getEmploymentResponse = publicV2ApiClient.viewEmploymentXml(user1OrcidId, putCode);
        assertNotNull(getEmploymentResponse);
        Employment employment = getEmploymentResponse.getEntity(Employment.class);
        assertNotNull(employment);
        assertEquals("affiliation:department-name", employment.getDepartmentName());

        ClientResponse getEmploymentSummaryResponse = publicV2ApiClient.viewEmploymentSummaryXml(user1OrcidId, putCode);
        assertNotNull(getEmploymentSummaryResponse);
        EmploymentSummary summary = getEmploymentSummaryResponse.getEntity(EmploymentSummary.class);
        assertNotNull(summary);
        assertEquals("affiliation:department-name", summary.getDepartmentName());
    }

    @Test
    public void testViewEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, educationToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getEducationResponse = publicV2ApiClient.viewEducationXml(user1OrcidId, putCode);
        assertNotNull(getEducationResponse);
        Education education = getEducationResponse.getEntity(Education.class);
        assertNotNull(education);
        assertEquals("education:department-name", education.getDepartmentName());

        ClientResponse getEducationSummaryResponse = publicV2ApiClient.viewEducationSummaryXml(user1OrcidId, putCode);
        assertNotNull(getEducationSummaryResponse);
        EducationSummary summary = getEducationSummaryResponse.getEntity(EducationSummary.class);
        assertNotNull(summary);
        assertEquals("education:department-name", summary.getDepartmentName());
    }

    @Test
    public void testViewPublicActivities() throws JSONException, InterruptedException, URISyntaxException {
        createActivities();
        ClientResponse activitiesResponse = publicV2ApiClient.viewActivities(user1OrcidId);
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        assertFalse(summary.getEducations().getSummaries().isEmpty());
        boolean found0 = false, found3 = false;
        for(EducationSummary education : summary.getEducations().getSummaries()) {
            if(education.getDepartmentName().equals("Education # 0")) {
                found0 = true;
            } else if (education.getDepartmentName().equals("Education # 3")) {
                found3 = true;
            }
        }
        
        assertTrue("One of the educations was not found: 0(" + found0 + ") 3(" + found3 + ")", found0 == found3 == true);
        
        assertFalse(summary.getEmployments().getSummaries().isEmpty());
        found0 = found3 = false;
        for(EmploymentSummary employment : summary.getEmployments().getSummaries()) {
            if(employment.getDepartmentName().equals("Employment # 0")) {
                found0 = true;
            } else if (employment.getDepartmentName().equals("Employment # 3")) {
                found3 = true;
            }
        }
        
        assertTrue("One of the employments was not found: 0(" + found0 + ") 3(" + found3 + ")", found0 == found3 == true);
        
        assertNotNull(summary.getFundings());
        found0 = found3 = false;
        for(FundingGroup group : summary.getFundings().getFundingGroup()) {
            for(FundingSummary funding : group.getFundingSummary()) {
                if(funding.getTitle().getTitle().getContent().equals("Funding # 0")) {
                    found0 = true;
                } else if(funding.getTitle().getTitle().getContent().equals("Funding # 3")) {
                    found3 = true;
                }
            }
        }
        
        assertTrue("One of the fundings was not found: 0(" + found0 + ") 3(" + found3 + ")", found0 == found3 == true);
        
        assertNotNull(summary.getWorks());
        found0 = found3 = false;
        for(WorkGroup group : summary.getWorks().getWorkGroup()) {
            for(WorkSummary work : group.getWorkSummary()) {
                if(work.getTitle().getTitle().getContent().equals("Work # 0")) {
                    found0 = true;
                } else if(work.getTitle().getTitle().getContent().equals("Work # 3")) {
                    found3 = true;
                }
            }
        }
        
        assertTrue("One of the works was not found: 0(" + found0 + ") 3(" + found3 + ")", found0 == found3 == true);
        

    }

    /**
     * TRY TO VIEW LIMITED INFO
     * */
    @Test
    public void testViewLimitedWorkAndWorkSummary() throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = publicV2ApiClient.viewWorkXml(user1OrcidId, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = publicV2ApiClient.viewWorkSummaryXml(user1OrcidId, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testViewLimitedFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);
        fundingToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, fundingToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = publicV2ApiClient.viewFundingXml(user1OrcidId, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = publicV2ApiClient.viewFundingSummaryXml(user1OrcidId, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testViewLimitedEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = publicV2ApiClient.viewEmploymentXml(user1OrcidId, putCode);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = publicV2ApiClient.viewEmploymentSummaryXml(user1OrcidId, putCode);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testViewLimitedEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, educationToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = publicV2ApiClient.viewEducationXml(user1OrcidId, putCode);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = publicV2ApiClient.viewEducationSummaryXml(user1OrcidId, putCode);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    private String getAccessToken() throws InterruptedException, JSONException {
        if (accessToken == null) {
            webDriver = new FirefoxDriver();
            webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);
            oauthHelper.setWebDriverHelper(webDriverHelper);
            accessToken = oauthHelper.obtainAccessToken(client1ClientId, client1ClientSecret, ScopePathType.ACTIVITIES_UPDATE.value(), user1UserName, user1Password,
                    redirectUri);
            webDriver.quit();
        }
        return accessToken;
    }

    public Activity unmarshallFromPath(String path, Class<? extends Activity> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Activity result = null;
            if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Funding.class.equals(type)) {
                result = (Funding) obj;
            } else if (Work.class.equals(type)) {
                result = (Work) obj;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public Object unmarshall(Reader reader, Class<? extends Activity> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

    private void createActivities() throws JSONException, InterruptedException, URISyntaxException {
        String accessToken = getAccessToken();
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);

        for (int i = 0; i < 4; i++) {            
            workToCreate.setPutCode(null);
            workToCreate.getWorkTitle().getTitle().setContent("Work # " + i);
            workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
            WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
            wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId(time + " Work Id " + i));
            wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
            workToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
            if (i == 0 || i == 3)
                workToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
            else if (i == 1)
                workToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);
            else
                workToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        for (int i = 0; i < 4; i++) {
            fundingToCreate.setPutCode(null);
            fundingToCreate.getTitle().getTitle().setContent("Funding # " + i);
            fundingToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
            FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
            fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
            fExtId.setValue(time + " funding Id " + i);
            fundingToCreate.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
            if (i == 0 || i == 3)
                fundingToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
            else if (i == 1)
                fundingToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);
            else
                fundingToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, fundingToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        for (int i = 0; i < 4; i++) {
            employmentToCreate.setPutCode(null);
            employmentToCreate.setRoleTitle("Employment # " + i);
            if (i == 0 || i == 3)
                employmentToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
            else if (i == 1)
                employmentToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);
            else
                employmentToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employmentToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        for (int i = 0; i < 4; i++) {
            educationToCreate.setPutCode(null);
            educationToCreate.setRoleTitle("Education # " + i);
            if (i == 0 || i == 3)
                educationToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
            else if (i == 1)
                educationToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.LIMITED);
            else
                educationToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, educationToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }
    }

    public void cleanActivities() throws JSONException, InterruptedException, URISyntaxException {
        // Remove all activities
        String token = getAccessToken();
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(user1OrcidId, token);
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        if (summary.getEducations() != null && !summary.getEducations().getSummaries().isEmpty()) {
            for (EducationSummary education : summary.getEducations().getSummaries()) {
                memberV2ApiClient.deleteEducationXml(user1OrcidId, education.getPutCode(), token);
            }
        }

        if (summary.getEmployments() != null && !summary.getEmployments().getSummaries().isEmpty()) {
            for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
                memberV2ApiClient.deleteEmploymentXml(user1OrcidId, employment.getPutCode(), token);
            }
        }

        if (summary.getFundings() != null && !summary.getFundings().getFundingGroup().isEmpty()) {
            for (FundingGroup group : summary.getFundings().getFundingGroup()) {
                for (FundingSummary funding : group.getFundingSummary()) {
                    memberV2ApiClient.deleteFundingXml(user1OrcidId, funding.getPutCode(), token);
                }
            }
        }

        if (summary.getWorks() != null && !summary.getWorks().getWorkGroup().isEmpty()) {
            for (WorkGroup group : summary.getWorks().getWorkGroup()) {
                for (WorkSummary work : group.getWorkSummary()) {
                    memberV2ApiClient.deleteWorkXml(user1OrcidId, work.getPutCode(), token);
                }
            }
        }
    }
}
