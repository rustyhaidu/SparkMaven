package ro.sparkmaven;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import ro.sparkmaven.dao.Sql2oCourseDao;
import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;

public class Sql2oCourseDaoTest {

	private Connection conn;
	private Sql2oCourseDao dao;

	@Before
	public void setUp() throws Exception {
		String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
		Sql2o sql2o = new Sql2o(connectionString, "", "");
		dao = new Sql2oCourseDao(sql2o);
		// Keep connection for the entire test
		conn = sql2o.open();
	}

	@After
	public void tearDown() {
		conn.close();
	}

	@Test
	public void addCourseSetsId() throws DaoException {
		Course course = newTestCourse();
		int originalCourseId = course.getId();
		dao.add(course);
		assertNotEquals(originalCourseId, course.getId());
	}

	@Test
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
	}
	private Course newTestCourse() {
		Course course = new Course("test", "http://www.test.com");
		return course;
	}

}
