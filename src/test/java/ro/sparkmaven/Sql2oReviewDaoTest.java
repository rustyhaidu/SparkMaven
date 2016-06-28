package ro.sparkmaven;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import ro.sparkmaven.dao.Sql2oCourseDao;
import ro.sparkmaven.dao.Sql2oReviewDao;
import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;
import ro.sparkmaven.model.Review;

public class Sql2oReviewDaoTest {

	private Connection conn;
	private Sql2oReviewDao reviewDao;
	private Sql2oCourseDao courseDao;
	private Course course;
	private Review review;

	@Before
	public void setUp() throws Exception {
		String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
		Sql2o sql2o = new Sql2o(connectionString, "", "");
		conn = sql2o.open();
		// Keep connection for the entire test
		reviewDao = new Sql2oReviewDao(sql2o);
		courseDao = new Sql2oCourseDao(sql2o);		
		course = newTestCourse();
		courseDao.add(course);
		
	}

	@After
	public void tearDown() {
		conn.close();
	}

	@Test
	public void addReviewSetsId() throws DaoException {		
		review = newTestReview(5);		
		int originalReviewId = review.getId();
		//System.out.println(originalReviewId);
		reviewDao.add(review);
		assertNotEquals(originalReviewId, review.getId());
	}
	
	@Test
	public void multipleReviewsAreFoundWhenTheyExistForACourse() throws Exception{
		review = newTestReview(5);	
		reviewDao.add(review);
		review = newTestReview(1);	
		reviewDao.add(review);
		
		
		List<Review> reviews = reviewDao.findCourseById(course.getId());
		assertEquals(2,reviews.size());
	}
	@Test(expected=DaoException.class)
	public void addingAReviewToANonExisingCourse() throws Exception{
		Review reviewWithNoExistingCourseId = new Review(42, 5, "Test Comment");
		reviewDao.add(reviewWithNoExistingCourseId);	
	}

	/*@Test
	public void addedCourseReturnedfromFindAll() throws DaoException {
		Course course = newTestCourse();
		dao.add(course);
		assertEquals(1, dao.findAll().size());
	}

	@Test
	public void noCoursesReturnsAnEmptyList() throws DaoException {
		assertEquals(0, dao.findAll().size());
	}

	@Test
	public void existingCoursesCanBeFoundById() throws DaoException {
		Course course = newTestCourse();
		dao.add(course);
		Course foundCourse = dao.findById(course.getId());
		assertEquals(course, foundCourse);
	}*/
	private Course newTestCourse() {
		course = new Course("test", "http://www.test.com");
		return course;
	}
	private Review newTestReview(int rating) {
		Review review = new Review(course.getId(), rating, "Test Comment");
		return review;
	}

}
