package ro.sparkmaven;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.google.gson.Gson;

import ro.sparkmaven.dao.Sql2oCourseDao;
import ro.sparkmaven.dao.Sql2oReviewDao;
import ro.sparkmaven.functionaltesting.ApiClient;
import ro.sparkmaven.functionaltesting.ApiResponse;
import ro.sparkmaven.model.Course;
import ro.sparkmaven.model.Review;
import spark.Spark;

public class ApiTest {
	public static final String PORT = "4568";
	public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
	private Connection con;
	private ApiClient client;
	private Gson gson;
	private Sql2oCourseDao courseDao;
	private Sql2oReviewDao reviewDao;

	@BeforeClass
	public static void startServer() {
		String[] args = { PORT, TEST_DATASOURCE };
		App.main(args);
	}

	@AfterClass
	public static void stopServer() {
		Spark.stop();
	}

	@Before
	public void setUp() throws Exception {
		Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
		courseDao = new Sql2oCourseDao(sql2o);
		reviewDao = new Sql2oReviewDao(sql2o);
		con = sql2o.open();
		client = new ApiClient("http://localhost:" + PORT);
		gson = new Gson();
	}

	@After
	public void tearDown() throws Exception {
		con.close();
	}

	private Course newTestCourse() {
		Course course = new Course("test", "http://www.test.com");
		return course;
	}

	@Test
	public void addingCoursesReturnsCreatedStatus() throws Exception {
		Map<String, String> values = new HashMap<>();
		values.put("name", "Test");
		values.put("url", "http://test.com");
		ApiResponse res = client.request("POST", "/courses", gson.toJson(values));
		assertEquals(201, res.getStatus());
	}

	@Test
	public void addedCourseCanBeAccessedById() throws Exception {
		Course course = newTestCourse();
		courseDao.add(course);

		ApiResponse res = client.request("GET", "/courses/" + course.getId());
		Course retrieved = gson.fromJson(res.getBody(), Course.class);
		assertEquals(course, retrieved);
	}

	@Test
	public void missingCourseReturnsNotFoundStatus() throws Exception {
		ApiResponse res = client.request("GET", "/courses/101");
		assertEquals(404, res.getStatus());
	}

	@Test
	public void addingReviewReturnsCreatedStatus() throws Exception {
		Course course = newTestCourse();
		courseDao.add(course);
		Map<String, Object> values = new HashMap<>();
		values.put("rating", 5);
		values.put("comment", "Test comment");

		ApiResponse res = client.request("POST", String.format("/courses/%d/reviews", course.getId()),
				gson.toJson(values));		
		assertEquals(201,res.getStatus());
	}
	@Test
	public void addingReviewToNotFoundCourseThrowsError() throws Exception {
		Course course = newTestCourse();
		courseDao.add(course);
		Map<String, Object> values = new HashMap<>();
		values.put("rating", 5);
		values.put("comment", "Test comment");

		ApiResponse res = client.request("POST",
				"/courses/42/reviews",
				gson.toJson(values));		
		assertEquals(500,res.getStatus());
	}
	@Test
	public void multipleReviewsReturnedForCourse() throws Exception{
		Course course = newTestCourse();
		courseDao.add(course);
		reviewDao.add(new Review(course.getId(),5,"Great Review!"));
		reviewDao.add(new Review(course.getId(),4,"Great Review!2"));
		
		ApiResponse res = client.request("GET",
				String.format("/courses/%d/reviews",course.getId()));					
		Review[] reviews = gson.fromJson(res.getBody(),Review[].class);
		assertEquals(2,reviews.length);		
	}

}
