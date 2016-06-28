package ro.sparkmaven.dao;

import java.util.List;

import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;
import ro.sparkmaven.model.Review;


public interface ReviewDao {
	void add(Review review) throws DaoException;
	
	
	List<Review> findAll();
	List<Review> findCourseById(int courseId);
}
