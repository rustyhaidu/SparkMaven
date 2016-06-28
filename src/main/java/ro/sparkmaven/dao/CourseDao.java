package ro.sparkmaven.dao;

import java.util.List;

import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;



public interface CourseDao {
	void add(Course course) throws DaoException;
	List<Course> findAll();
	Course findById(int id);
}
