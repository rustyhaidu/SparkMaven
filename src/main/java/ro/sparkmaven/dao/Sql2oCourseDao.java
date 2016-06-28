package ro.sparkmaven.dao;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;

public class Sql2oCourseDao implements CourseDao {
	private Sql2o sql2o;

	public Sql2oCourseDao(Sql2o sql2o) {
		super();
		this.sql2o = sql2o;
	}

	@Override
	public void add(Course course) throws DaoException {
		String query = "INSERT INTO courses(name,url) VALUES (:name, :url)";
		try (Connection con = sql2o.open()) {
			int id = (int) con.createQuery(query).bind(course).executeUpdate().getKey();
			course.setId(id);
		} catch (Sql2oException ex) {
			System.out.println("Exception" + ex);
			throw new DaoException(ex, "Problem adding Course");
		}
	}

	@Override
	public List<Course> findAll() {
		try (Connection con = sql2o.open()) {
			return con.createQuery("SELECT * from courses").executeAndFetch(Course.class);
		}

	}

	@Override
	public Course findById(int id) {
		try (Connection con = sql2o.open()) {
			return con.createQuery("SELECT * from courses where id = :id").addParameter("id", id)
					.executeAndFetchFirst(Course.class);
		}
	}

}
