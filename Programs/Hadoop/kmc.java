package brandon.shavedmullet;
 	
	import java.io.*;
 	import java.io.IOException;
 	import java.util.*;
	import java.nio.ByteBuffer;
 
	import org.apache.hadoop.fs.FSDataInputStream;
	import org.apache.hadoop.fs.FSDataOutputStream;	
	import org.apache.hadoop.fs.FileSystem;
	import org.apache.hadoop.fs.FileUtil;
	import org.apache.hadoop.fs.permission.FsPermission;
	import org.apache.hadoop.fs.permission.FsAction;
 	import org.apache.hadoop.fs.Path;
 	import org.apache.hadoop.conf.*;
 	import org.apache.hadoop.io.*;
 	import org.apache.hadoop.mapreduce.Job;
	import org.apache.hadoop.mapreduce.Mapper;
	import org.apache.hadoop.mapreduce.Reducer;
	import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
	import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
 	import org.apache.hadoop.util.*;
 	
class Brands
{
	static int noc,no=0;
        static double[][] means=new double[20][2];
	//static double[][] tmpmeans=new double[20][2];
        public static boolean chkmeans()
        {
        	int i=0,j=0;
                
                for(i=0;i<noc-1;i++)
                {
			for(j=1;j<noc;j++)
			{
                       		if((means[i][0]!=means[j][0])||(means[i][1]!=means[j][1]))
                       		{
                       		      	return(true);
                       		}
	                }
		}
        	return(false);
        }
	public static void setnoc(int tmp)
	{
		noc=tmp;
		//means=new String[noc];
	}
	public static void setno(int tmp)
        {
                no=tmp;
        }
	public static int retno()
        {
                return(no);
        }
	public static int retnoc()
	{
		return(noc);
	}
	public static double retmeans(int i,int j)
	{
		return(means[i][j]);
	}	
	public static void setmeans(int i,int j,double tmp)
	{
		means[i][j]=tmp;
	}
/*
	public static void settmp(int a,int b,double tmp)
	{
		tmpmeans[a][b]=tmpmeans[a][b]+tmp;
	}
	public static double rettmpmean(int a,int b)
	{
		return(tmpmeans[a][b]);
	} 
	public static void avgtmp(int a,int b)
	{
		tmpmeans[a][b]=tmpmeans[a][b]/noc;
	}
*/
}


public class kmc
{
 	   	public static class Map extends Mapper<Object, Text, IntWritable, Text> 
		{
 	     		private Text word = new Text();
			static Brands global=new Brands();

 	     		public void map(Object key, Text value, Context context) throws IOException,InterruptedException 
			{
				int m=0,i,counter,clustno;
				String tmpmeans;
			

				Configuration conf=context.getConfiguration();

				String a = conf.get("para");
				counter=conf.getInt("count",0);
				String o=conf.get("outf");

				FileSystem fs=FileSystem.get(conf);
				global.setnoc(Integer.parseInt(a));

				String line =value.toString();
				//static double[][] tmpmeans=new int[global.retnoc()][2];
				
				/*int k,l;
				for(k=0;k<global.retnoc();k++)
				{
					for(l=0;l<2;l++)
					{
			       			tmpmeans[k][l]=0;
					}
				}*/			

				StringTokenizer tokenN = new StringTokenizer(line);
                                double[] tmpN=new double[2];
                                double[] tmpM=new double[2];
                                double[] dist=new double[global.retnoc()];
                                
				tmpN[0]=Double.parseDouble(tokenN.nextToken());
                                tmpN[1]=Double.parseDouble(tokenN.nextToken());
				

				if(global.retno()<global.retnoc())
				{
					if(counter==0)
					{
						global.setmeans(global.retno(),0,tmpN[0]);
						global.setmeans(global.retno(),1,tmpN[1]);
						m=global.retno();	
 	       					global.setno(m+1);
					}
					else
					{
						File inPath2=new File("/home/hduser/part-r-00000");
						if(inPath2.exists())
						{
							inPath2.delete();
						}
						Path inPath = new Path(o+"/o"+(counter-1)+"/part-r-00000");//o+"/o"+(counter-1)+"/part-r-00000");
						Path inPath1=new Path("/home/hduser/");
						//String inPath2="/home/hduser/part-r-00000";
						fs.copyToLocalFile(inPath,inPath1);
						/*FSDataInputStream ivc=fs.open(inPath);
						InputStream ivc1=new FileInputStream(ivc.getFileDescriptor());
						Reader re=new InputStreamReader(ivc1);*/
						BufferedReader in=new BufferedReader(new FileReader(inPath2)); 
						for(i=0;i<global.retnoc();i++)
						{
							tmpmeans=in.readLine();
							StringTokenizer tokentmp=new StringTokenizer(tmpmeans);
							double[] tmpdb=new double[2];
							clustno=Integer.parseInt(tokentmp.nextToken());
							tmpdb[0]=Double.parseDouble(tokentmp.nextToken());
                                			tmpdb[1]=Double.parseDouble(tokentmp.nextToken());

							
							
							global.setmeans(clustno,0,tmpdb[0]);
							global.setmeans(clustno,1,tmpdb[1]);
							global.setno(global.retnoc());
						}
						in.close();
					}
				}
				if(global.retno()>=global.retnoc())
				{
					for(i=0;i<global.retnoc();i++)
					{
						tmpM[0]=global.retmeans(i,0); 
						tmpM[1]=global.retmeans(i,1);
						dist[i] = Math.sqrt(Math.pow((tmpM[0]-tmpN[0]),2)+Math.pow((tmpM[1]-tmpN[1]),2));
					}
			
					m=0;					

					for(i=1;i<global.retnoc();i++)
					{
						if(dist[m]>dist[i])
						{
							m=i;//finding the cluster
						}	
					}
				}
				//c.setBoolean("chk",global.chkmeans());		
				//global.settmp(m,0,global.rettmpmean(m,0)+tmpN[0]);
                                //global.settmp(m,1,global.rettmpmean(m,1)+tmpN[1]);
				
				/*if(global.retno()==global.retnoc())
				{
					global.setno(global.retnoc()+1);
				}*/
				
				IntWritable clusterno= new IntWritable(m);
				word.set(line);
				context.write(clusterno,word);

 	     		}
 	   	}
 	
 	   	public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> 
		{
			//FileWriter mullet=new FileWriter("/home/hduser/Major Work/Programs/Hadoop/kmeans/means.txt");
			Text word = new Text();
			//static int mullet;
			//public static final FsAction ALL;
 	     		public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
			{
				int clusterno=0;
				double[] tmp= new double[2];
				double[] means={0,0};
				double[] tmpmeans=new double[2];
				String strmeans;
				Configuration conf = context.getConfiguration();
                                //int noc=Integer.parseInt(conf.get("para"));
                                //int cluster=values.iterator().next().get();
				//int counter=conf.getInt("count",0);
				//FsAction perm=new FsAction(ALL);
				//File f=new File("/home/hduser/Major Work/Programs/Hadoop/kmeans/means.txt");
				
				/*if(mullet==0)
				{
					f.delete();
				}*/
				//BufferedWriter out=new BufferedWriter(new FileWriter("/home/hduser/Major Work/Programs/Hadoop/kmeans/means"+cluster+".txt"));
							

				//FileSystem fs=FileSystem.get(conf);
				//FSDataOutputStream out=fs.create(outFile,true);//fs,outFile,new FsPermission("777"));//ALL,ALL,ALL));
				//FileUtil n =new FileUtil();
				//n.chmod("ref","777",true);
				while(values.iterator().hasNext())
				{
					clusterno++;			
		
                                	String line=values.iterator().next().toString();
                                	StringTokenizer token=new StringTokenizer(line);                            
                       		//double means[][]=new double[noc][2];
			
                	                tmp[0]=Double.parseDouble(token.nextToken());
                        	        tmp[1]=Double.parseDouble(token.nextToken());
				
                        	        means[0]=means[0]+tmp[0];
                               		means[1]=means[1]+tmp[1]; 
				
					tmpmeans[0]=means[0]/clusterno;
					tmpmeans[1]=means[1]/clusterno;
				}

				strmeans=String.valueOf(tmpmeans[0])+" "+String.valueOf(tmpmeans[1]);

				/*out.write(strmeans[cluster]);
				out.close();*/		
				word.set(strmeans);
 	       			context.write(key,word);
 	     		}
		}
	
	public static class Reduce1 extends Reducer<IntWritable, Text, Text, IntWritable> 
	{
             	public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
		{
           
               		while (values.iterator().hasNext()) 
			{    
               			context.write(values.iterator().next(),key);
			}
             	}
        }
 	   
 	public static void main(String[] args) throws Exception 
	{

		int counter=0,i,j,noc;
		boolean chk=true;
		noc = Integer.parseInt(args[2]);
		double[][] tmpdb1=new double[noc][2];

		double[][] tmp=new double[noc][2];
  
		
		Configuration conf = new Configuration();
		FileSystem fs=FileSystem.get(conf);
		conf.set("outf",args[1]);
		
		do
		{
			String tmpmeans1,tmpmeans2;
			if(counter>0)
			{
				i=0;			
                                File inPath2=new File("/home/hduser/part-r-00000");
                               	if(inPath2.exists())
                                {
                                      inPath2.delete();
                                }
				Path inPath = new Path(args[1]+"/o"+(counter-1)+"/part-r-00000");//args[1]+"/o"+(counter-1)+"/part-r-00000");
                                Path inPath1=new Path("/home/hduser/");
                                                        //String inPath2="/home/hduser/part-r-00000";
                                fs.copyToLocalFile(inPath,inPath1);


				BufferedReader in1=new BufferedReader(new FileReader(inPath2));
				while(i<noc)
				{
                               		tmpmeans1=in1.readLine();
                               		StringTokenizer tokentmp1=new StringTokenizer(tmpmeans1);
                                //double[][] tmpdb1=new double[Integer.parseInt(args[2])][2];
					tokentmp1.nextToken();
                                	tmpdb1[i][0]=Double.parseDouble(tokentmp1.nextToken());
                                	tmpdb1[i][1]=Double.parseDouble(tokentmp1.nextToken());
					i++;
				}
				if(counter>1)
				{
					for(i=0;i<noc;i++)
					{
						if((tmp[i][0]!=tmpdb1[i][0])||(tmp[i][1]!=tmpdb1[i][1]))
						{
							chk=true;
							break;
						}
						else
						{
							chk=false;
						}
					}
				}
				for(i=0;i<noc;i++)
                        	{
					for(j=0;j<2;j++)
					{
						tmp[i][j]=tmpdb1[i][j];
					}
				}
			}

                               /* BufferedReader in2=new BufferedReader(new FileReader("/home/hduser/Major Work/Programs/Hadoop/kmeans/means"+(i+1)+".txt"));
                                tmpmeans2=in2.readLine();
                                StringTokenizer tokentmp2=new StringTokenizer(tmpmeans2);
                                double[] tmpdb2=new double[2];
                                tmpdb2[0]=Double.parseDouble(tokentmp2.nextToken());
                                tmpdb2[1]=Double.parseDouble(tokentmp2.nextToken());
				if((tmpdb1[0]!=tmpdb2[0])||(tmpdb1[1]!=tmpdb2[1]))
				{
					chk=true;
					break;
				}
				else
				{
					chk=false;
				}*/
			
			conf.set("para",args[2]);
			//fs.delete(new Path(args[1]),true);
			conf.setInt("count",counter);		

		      	Job job = new Job(conf,"cluster");

 			job.setJarByClass(kmc.class);
		
	 	     	job.setMapOutputKeyClass(IntWritable.class);
 		     	job.setMapOutputValueClass(Text.class);
			/*if(chk==false)
			{
				job.setNumReduceTasks(0);
			}*/
 		
 		     	job.setMapperClass(Map.class);
			if(chk==true)
			{
 		     		job.setCombinerClass(Reduce.class);
 		     		job.setReducerClass(Reduce.class);
				job.setOutputKeyClass(IntWritable.class);
                        	job.setOutputValueClass(Text.class);

			}
			else
			{
				//job.setCombinerClass(Reduce1.class);
                                job.setReducerClass(Reduce1.class);
				job.setOutputValueClass(IntWritable.class);
	                        job.setOutputKeyClass(Text.class);
			}
 	
 		     	//job.setInputFormat(TextInputFormat.class);
 		     	//job.setOutputFormat(TextOutputFormat.class);
 	
 		     	FileInputFormat.setInputPaths(job, new Path(args[0]));
 	    	 	FileOutputFormat.setOutputPath(job, new Path(args[1]+"/o"+counter));
		
			//conf.setInt("count",counter);

 	     	
			job.waitForCompletion(true);
			counter++;
			
		}while(chk);//conf.getBoolean("chk",false));
		File inPath2=new File("/home/hduser/part-r-00000");
		if(inPath2.exists())
                {
                	inPath2.delete();
                }
 	}
 }
