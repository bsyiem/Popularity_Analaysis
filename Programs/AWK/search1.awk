#!bin/awk
BEGIN {count=0}
{for(i=1;i<=NF;i++){if($i~/\<students\>/){count++}}}
END {print count;}

