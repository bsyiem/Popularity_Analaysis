package brandon.shavedmullet;
 	
	import java.io.*;
 	import java.io.IOException;
 	import java.util.*;
 	
	import org.apache.hadoop.fs.FileSystem;
	import org.apache.hadoop.fs.FSDataInputStream;
	import org.apache.hadoop.fs.FSDataOutputStream;
 	import org.apache.hadoop.fs.Path;
 	import org.apache.hadoop.conf.*;
 	import org.apache.hadoop.io.*;
 	import org.apache.hadoop.mapreduce.Job;
	import org.apache.hadoop.mapreduce.Mapper;
	import org.apache.hadoop.mapreduce.Reducer;
	import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
	import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
 	import org.apache.hadoop.util.*;
 	
 	public class CandidateC {
 	
 	   public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
 	     private final static IntWritable one = new IntWritable(1);
 	     private Text word = new Text();
 	
 	     public void map(LongWritable key, Text value, Context context) throws IOException ,InterruptedException {
 	       String line = value.toString();
 	       
	       //String[] candidates=new String[]{"Modi","Gandhi","Kejriwal"};
		File folder =new File("/home/hduser/Major Work/Programs/Hadoop/references");
		File[] listOfFiles=folder.listFiles();
	      int i=0;
		while(i<listOfFiles.length)
		{
	       BufferedReader br = new BufferedReader(new FileReader("/home/hduser/Major Work/Programs/Hadoop/references/"+listOfFiles[i].getName()));
		   String line1;
           while ((line1 = br.readLine()) != null) 
		   {
				if(line.toLowerCase().contains(line1.toLowerCase()))
				{
					word.set(listOfFiles[i].getName().substring(0,listOfFiles[i].getName().lastIndexOf(".")));
					context.write(word,one);
				}
		   }
		   br.close();
		   i++;
		}
 	     }
 	   }
 	
 	   public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
 	     public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,InterruptedException {
 	       int sum = 0;
 	       while (values.iterator().hasNext()) {
 	         sum += values.iterator().next().get();
 	       }
 	       context.write(key, new IntWritable(sum));
 	     }
 	   }
 	
 	   public static void main(String[] args) throws Exception 
	   {
 	     Configuration conf =new Configuration();  
		
	     Job job = new Job(conf,"candidatecount");
 	     job.setJarByClass(CandidateC.class);
 	
 	     job.setOutputKeyClass(Text.class);
 	     job.setOutputValueClass(IntWritable.class);
 	
 	     job.setMapperClass(Map.class);
 	     job.setCombinerClass(Reduce.class);
 	     job.setReducerClass(Reduce.class);
 	
 	     //conf.setInputFormat(TextInputFormat.class);
 	     //conf.setOutputFormat(TextOutputFormat.class);
 	
 	     FileInputFormat.setInputPaths(job, new Path(args[0]));
 	     FileOutputFormat.setOutputPath(job, new Path(args[1]));
	
 	     job.waitForCompletion(true);
 	   }
 	}
