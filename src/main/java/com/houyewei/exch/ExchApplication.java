package com.houyewei.exch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class ExchApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(ExchApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ExchApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) throws Exception {
		log.info("creating tables");
		jdbcTemplate.execute("DROP TABLE IF EXISTS customers");

//		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("create table customers(" +
				"id serial, first_name VARCHAR (255), last_name varchar (255)" +
				")");
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream().map(
				name-> name.split(" ")
		).collect(Collectors.toList());

		splitUpNames.forEach(name -> log.info(String.format("inserting customer record for %s %s",  name[0], name[1])));


		jdbcTemplate.batchUpdate("insert into customers(first_name, last_name) values(?,?)", splitUpNames);
		log.info("Querying for customer records where first_name = 'Josh':");

		jdbcTemplate.query("select id, first_name, last_name from customers where first_name = ? ",
				new Object [] {"Josh"}, (rs, rowNum) -> new Customer(rs.getLong("id"),
						rs.getString("first_name"),
						rs.getString("last_name"))
		).forEach(customer -> log.info(customer.toString()));

	}
}
