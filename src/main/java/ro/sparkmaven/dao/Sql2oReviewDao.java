package ro.sparkmaven.dao;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;
import ro.sparkmaven.model.Review;

public class Sql2oReviewDao implements ReviewDao{
	private Sql2o sql2o;

	public Sql2oReviewDao(Sql2o sql2o) {
		super();
		this.sql2o = sql2o;
	}

	@Override
	public void add(Review review) throws DaoException {
		String query = "INSERT INTO reviews(course_id,rating,comment) VALUES (:courseId, :rating,:comment)";
		try (Connection con = sql2o.open()) {
			int id = (int) con.createQuery(query)
					.bind(review)
					.executeUpdate()
					.getKey();
			review.setId(id);
		} catch (Sql2oException ex) {
			System.out.println("Exception" + ex);
			throw new DaoException(ex, "Problem adding Review");
		}
		
	}

	@Override
	public List<Review> findAll() {
		try(Connection con = sql2o.open()){
			return con.createQuery("SELECT * from reviews").executeAndFetch(Review.class);
		}
	}

	@Override
	public List<Review> findCourseById(int courseId) {
		try (Connection con = sql2o.open()) {
			return con.createQuery("SELECT * from reviews where course_id = :courseId")
					.addColumnMapping("COURSE_ID", "courseId")
					.addParameter("courseId", courseId)
					.executeAndFetch(Review.class);
		}
	}

}
