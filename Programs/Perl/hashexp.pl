#!D:\Perl\perl\bin\perl

use strict;
use warnings;
use Web::Scraper;
use URI;

open FH, ">table.txt" or die $!;

my $url="http://www.w3schools.com/html/html_tables.asp";

my %hash;
my $key;
my $value;
my $data= scraper{
			#process '//table[contains(@class,"reference")]/tr/td[1]','name[]'=>'TEXT'; #takes from classes containing reference and others
			process '//table[@class = "reference"]/tr/td[1]','name[]'=>'TEXT'; #takes only from class = reference
		};
my $res=$data->scrape(URI->new($url));
for my $i(0 .. $#{$res->{name}})
{
	if(exists $hash{$res->{name}[$i]})
	{
		$hash{$res->{name}[$i]}++;
	}
	else
	{
		$hash{$res->{name}[$i]}=1;
	}
}
while(($key,$value)=each(%hash))
{
	print  $key." ".$value."\n";
	print FH $key." ".$value."\n";
}
#print %hash;
close(FH);