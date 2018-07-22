#!bin/awk
BEGIN {$count=0}
{
	for($i=0;$i<=NF;$i++)
	{
		if($i~/\<students\>/)
		{
			$count++
		}
	}
}
END {print $count}

