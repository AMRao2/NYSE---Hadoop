package com.rao;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class TotalStock {
	
	public static class MyMapper extends Mapper<LongWritable,Text,Text,LongWritable> {
		
		private Text stksym = new Text();
		private LongWritable stkvolume = new LongWritable(1);
		
		public void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException{
			
			String[] w = value.toString().split(",");
			if(!w[0].equals("exchange"))
			{
			stksym.set(new Text(w[1]));
			stkvolume.set(Long.parseLong(w[7]));
			context.write(stksym, stkvolume);
			
			}
		}
		
		
	}
	
    public static class MyReducer extends Reducer<Text, LongWritable,Text, LongWritable>{
    	
    	public void reduce(Text key,Iterable<LongWritable> values, Context context) throws IOException, InterruptedException
    	{
    		    	    
    	    long sum = 0;
  	        for (LongWritable val : values) {
  	        	sum += val.get();
  	        }
    	      
    	      
    	      context.write(key, new LongWritable(sum));
    		
    	}
    	
    }
    
public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = new Job(conf, "TotalStock");
		
		job.setJarByClass(TotalStock.class);
		
		
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
	//	job.setPartitionerClass(MyPartition.class);
		job.setNumReduceTasks(1);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
			
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	
		job.waitForCompletion(true);
	}
    
}

