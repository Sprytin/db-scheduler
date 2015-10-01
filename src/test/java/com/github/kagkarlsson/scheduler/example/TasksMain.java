package com.github.kagkarlsson.scheduler.example;

import com.github.kagkarlsson.scheduler.*;
import com.github.kagkarlsson.scheduler.task.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.google.common.collect.Lists.newArrayList;

public class TasksMain {
	private static final Logger LOG = LoggerFactory.getLogger(TasksMain.class);

	public static void main(String[] args) throws Throwable {
		try {
			final HsqlTestDatabaseRule hsqlRule = new HsqlTestDatabaseRule();
			hsqlRule.before();
			final DataSource dataSource = hsqlRule.getDataSource();

			recurringTask(dataSource);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

	private static void recurringTask(DataSource dataSource) {

		final MyHourlyTask hourlyTask = new MyHourlyTask();

		final Scheduler scheduler = Scheduler
				.create(dataSource)
				.recurringTasks(newArrayList( hourlyTask ))
				.build();

		// Recurring task is automatically scheduled
		scheduler.start();
	}

	public static class MyHourlyTask extends RecurringTask {

		public MyHourlyTask() {
			super("task_name", FixedDelay.of(Duration.ofHours(1)));
		}

		@Override
		public void execute(TaskInstance taskInstance, ExecutionContext executionContext) {
			System.out.println("Executed!");
		}
	}

	public static class MyAdhocTask extends OneTimeTask {

		public MyAdhocTask() {
			super("adhoc_task_name");
		}

		@Override
		public void execute(TaskInstance taskInstance, ExecutionContext executionContext) {
			System.out.println("Executed!");
		}
	}

	private static void adhocExecution(Scheduler scheduler, MyAdhocTask myAdhocTask) {

		// Schedule the task for execution a certain time in the future
		scheduler.scheduleForExecution(LocalDateTime.now().plusMinutes(5), myAdhocTask.instance("1045"));
	}

}
