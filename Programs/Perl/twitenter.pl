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

#use Scalar::Util qw(looks_like_number);

open FH, ">$ARGV[0]" or die $!;

#coded by Brandon V.Syiem

my $mech = WWW::Mechanize->new(autocheck => [1]);
my $url  = 'https://twitter.com/';
my $username = 'shavedmullet@gmail.com';
my $password = 'uratool';

#SHAVEDMULLET

$mech->get($url);
#my $old = $mech->text();
#print $old;
print "\n\n";

$mech->submit_form(
	form_number => 2,
    	fields   => {
        	"session[username_or_email]" => $username,
		"session[password]" => $password,
    		},
	);

#$mech->form_number(0);
#$mech->field("session[username_or_email]",$username);
#$mech->field("session[password]",$password);
#$mech->submit_form();

#my $new = $mech->text();
#my $newuri=$mech->uri();
#print $new;

#Brandon's DataScraper
my $data= scraper{
			process '//strong[@class = "fullname js-action-profile-name show-popup-with-id"]','name[]'=>'TEXT'; #takes from classes containing reference and others
			process '//a[@class="account-group js-account-group js-action-profile js-user-profile-link js-nav"]','url[]'=>'@href'; #takes only from class = reference
		};

#my $res=$data->scrape(URI->new($newuri));
my $res=$data->scrape($mech->response());

my $data1=scraper{
			process '//a[@data-nav="followers"]/strong','follow1'=>'@title';
			process '//a[@data-nav="followers"]','follow2'=>'@title';
};
for my $i(0 .. $#{$res->{name}})
{
	print $res->{name}[$i];
	print "\t";
	print FH $res->{name}[$i];
        print FH "\t";
	#print $res->{url}[$i]."\t";
	#my $urls=$mech->follow_link(url =>$res->{url}[$i]);
	my $res1=$data1->scrape(URI->new($res->{url}[$i]));
	if($res1->{follow2} ne '')
	{
		my @follows=split(' ',$res1->{follow2});
		print $follows[0]."\n";
		print FH $follows[0]."\n";
	}
	else
	{
		print $res1->{follow1}."\n";
                print FH $res1->{follow1}."\n";

	}
}
