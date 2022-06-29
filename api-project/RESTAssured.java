package project;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class RESTAssured {

    RequestSpecification requestSpec;
    RequestSpecification requestSpec1;
    ResponseSpecification responseSpec;
    ResponseSpecification responseSpec1;
    ResponseSpecification responseSpec2;
    String tokenGenerated = "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";
    String sshKey;
    int sshId;

    @BeforeClass
    public void setupRequestSpecification() {
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://api.github.com/").setBasePath("user/keys")
                //.addHeader()
                .build().log().all();
        requestSpec1 = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://api.github.com/").setBasePath("user/keys")
                .build().log().all();
        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType("application/json")
                .build();
        responseSpec1 = new ResponseSpecBuilder()
                // Check status code in response
                .expectStatusCode(200)
                // Check response content type
                .expectContentType("application/json")
                // Check if response contains name property
                .build();
        responseSpec2 = new ResponseSpecBuilder()
                // Check status code in response
                .expectStatusCode(204)
                // Check response content type
              //  .expectContentType("application/json")
                // Check if response contains name property
                .build();
    }

    @Test(priority=1)
    public void addSSHKey()
    {
        String reqBody = "{\"title\": \"TestAPIKey\", \"key\": \"ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIGJDdZx4d4O+PcwBZrVTZxWglW4QZ7a3MXHLxBXLz6Xs\"}";
        Response response = given().spec(requestSpec).auth().oauth2(tokenGenerated)
                .body(reqBody)
                .when().post();

        response.then().spec(responseSpec).log().all();
        sshId = response.jsonPath().get("id");
        sshKey = response.jsonPath().get("key");
        System.out.println("Id is.............."+sshId);
        System.out.println("Ssh key is.............."+sshKey);
    }
    @Test( priority=2)
    public void getSSHKey() {
        Response response = given().spec(requestSpec).auth().oauth2(tokenGenerated)// Use requestSpe
                .when().get(); // Send GET request

        // Print response
        System.out.println("SSH Key added to the account is..... "+response.asPrettyString());
        // Assertions
        response.then()
                .spec(responseSpec1); // Use responseSpec
        Reporter.log("SSH Key added to Account is.... "+response.toString());

    }
    @Test(priority=3)
    public void deleteSSHKey() {
        Response response = given().spec(requestSpec1).
                pathParam("keyID",sshId).auth().oauth2(tokenGenerated) // Use requestSpec
                .when().delete("/{keyID}"); // Send Delete Request

        // Assertions
        response.then().spec(responseSpec2);
        Reporter.log("SSH Key deleted from Account is.... "+response.toString());
    }
}

