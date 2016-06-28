package ro.sparkmaven;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.exception;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.port;

import org.sql2o.Sql2o;
import com.google.gson.Gson;
import ro.sparkmaven.dao.CourseDao;
import ro.sparkmaven.dao.ReviewDao;
import ro.sparkmaven.dao.Sql2oCourseDao;
import ro.sparkmaven.dao.Sql2oReviewDao;
import ro.sparkmaven.exc.ApiError;
import ro.sparkmaven.exc.DaoException;
import ro.sparkmaven.model.Course;
import ro.sparkmaven.model.Review;
import spark.Route;

public class App {
	public static void main(String[] args) {
		String datasource = "jdbc:h2:~/reviews.db";
		if (args.length > 0) {
			if (args.length != 2) {
				System.out.println("Java Api <port> <datasource>");
			}
			port(Integer.parseInt(args[0]));
			datasource = args[1];
		}

		/*
		 * String connectionString =
		 * "jdbc:h2:~/reviews.db;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
		 * Sql2o sql2o = new Sql2o(connectionString, "", "");
		 */
		Sql2o sql2o = new Sql2o(String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", datasource), "", "");
		CourseDao courseDao = new Sql2oCourseDao(sql2o);
		ReviewDao reviewDao = new Sql2oReviewDao(sql2o);
		Gson gson = new Gson();

		post("/courses", "application/json", (req, res) -> {
			Course course = gson.fromJson(req.body(), Course.class);
			courseDao.add(course);
			res.status(201);
			res.type("application/json");
			return course;
		} , gson::toJson);

		get("/courses", "application/json", (req, res) -> courseDao.findAll(), gson::toJson);

		get("/courses/:id", "application/json", (req, res) -> {
			int id = Integer.parseInt(req.params("id"));
			Course course = courseDao.findById(id);
			if (course == null) {
				throw new ApiError(404, "Could not find course by Id");
			}
			return course;
		} , gson::toJson);

		post("/courses/:courseId/reviews", "application/json", (req, res) -> {
			int courseId = Integer.parseInt(req.params("courseId"));
			Review review = gson.fromJson(req.body(), Review.class);
			review.setCourseId(courseId);

			try {
				reviewDao.add(review);
			} catch (DaoException ex) {
				throw new ApiError(500, ex.getMessage());
			}
			res.status(201);
			return review;
		} , gson::toJson);

		get("/courses/:courseId/reviews", "application/json", (req, res) -> {
			int courseId = Integer.parseInt(req.params("courseId"));
			return reviewDao.findCourseById(courseId);
		},gson::toJson);

		exception(ApiError.class, (exc, req, res) -> {
			ApiError err = (ApiError) exc;
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("status", err.getStatus());
			jsonMap.put("errorMessage", err.getMessage());
			res.type("application/json");
			res.status(err.getStatus());
			res.body(gson.toJson(jsonMap));
		});

		after((req, res) -> {
			res.type("application/json");
		});
	}
}
