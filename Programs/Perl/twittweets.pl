#!/usr/bin/perl

use LWP::UserAgent;
use WWW::Mechanize;
#use strict;
use warnings;
use Web::Scraper;
use URI;
use HTTP::Cookies;
use HTTP::Request::Common;
#use Crypt::SSLeay
#use Time::localtime

#use Scalar::Util qw(looks_like_number);
my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)=localtime(time);

$mon=$mon+1;
$year=$year+1900;
#open FH, ">$ARGV[0]" or die $!;

#coded by Brandon V.Syiem

my $mech = WWW::Mechanize->new(autocheck => [1]);
my $url  = 'https://twitter.com/';
my $username = 'bsyiem92@gmail.com';
my $password = 'uratool';

#SHAVEDMULLET

$mech->get($url);
print "\n\n";

$mech->submit_form(
	form_number => 2,
    	fields   => {
        	"session[username_or_email]" => $username,
		"session[password]" => $password,
    		},
	);
$mech->follow_link(url => '/following');
#print $mech->text();
#Brandon's DataScraper for login SHAVEDMULLET
my $data= scraper{
			process '//span[@class = "u-linkComplex-target"]','name[]'=>'TEXT'; #takes name of ppl u are following
			process '//a[@class="ProfileCard-screennameLink u-linkComplex js-nav"]','url[]'=>'@href'; #takes url of ppl ur following
		};

#Brandon's DATASCRAPER for NO of FOLLOWERS 
my $data1=scraper{
			process '//p[@class="ProfileTweet-text js-tweet-text u-dir"]','tweet[]'=>'TEXT'; #For customized twitter acc
			process '//p[@class="js-tweet-text tweet-text"]','tweet[]'=>'TEXT'; #For non-Customized Twitter acc 
};

my $res=$data->scrape($mech->response());

print $#{$res->{name}}."\n";
print $#{$res->{url}}."\n";
for my $i(2 .. $#{$res->{name}})
{
	print $res->{name}[$i];
	print "\n-------------------------------------------------------------\n";
	open FH,">>/home/hduser/Major\ Work/input/toanalyze/".$res->{name}[$i]."_".$mon."_".$year.".txt" or die $!;
	#print FH $res->{name}[$i];
        #print FH "\t";
	my $res1=$data1->scrape(URI->new($res->{url}[$i-2]));

#@title of customized = "number followers" so we extract only the number
	#if($res1->{follow2} ne '')
	#{
	#	my @follows=split(' ',$res1->{follow2});
	#	print $follows[0]."\n";
	#	print FH $follows[0]."\n";
	#}
	#else
	#{
	for my $j(0 .. $#{$res1->{tweet}})
	{
		print $res1->{tweet}[$j]."\n";
		print FH " ".$res1->{tweet}[$j]." \n";
        #       print FH $res1->{follow1}."\n";
	}
close(FH);
	#}
}
#close(FH);
