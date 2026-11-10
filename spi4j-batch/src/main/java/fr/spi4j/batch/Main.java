package fr.spi4j.batch;

import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
	
		TaskManager manager = new TaskManager(1, 10, 1000, TimeUnit.MILLISECONDS, 1000, 1000, TimeUnit.MILLISECONDS);
		
		Job job = new Job(1000, TimeUnit.MILLISECONDS);
		
//		Chunk<IN, OUT> myChunk = new Chunk<IN, OUT>("myChunk1") {
//		};
		
		Tasklet tasklet = new Tasklet("myTasket") {
			
			@Override
			public void run() {
				System.out.println("Execution de myTasklet");
				
			}
		};
		
		job.add(tasklet);
		manager.taskStart(job);
		//manager.shutdown();
	}

}
