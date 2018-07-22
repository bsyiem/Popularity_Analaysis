#!D:\Perl\perl\bin\perl

use LWP::UserAgent;
use WWW::Mechanize;
use strict;
use warnings;
use Web::Scraper;
use URI;

my $mech = WWW::Mechanize->new(autocheck => [1]);
my $url  = 'https://www.facebook.com/';
my $username = 'shavedmullet@gmail.com';
my $password = 'webejamming1514';
$mech->get($url);
my $old = $mech->text();
#print $old;
print "\n\n";
$mech->submit_form(
	form_id => 'login_form',
    fields   => {
        email   => $username,
		pass => $password,
    },
);
my $new = $mech->text();
my $newuri=$mech->uri();
#print $new;
my $data= scraper{
			#process '//body','status[]'=>'@class'; #takes from classes containing reference and others
			process '//a[@class="navLink bigPadding"]','status[]'=>'TEXT','url[]'=>'@href'; #takes only from class = reference
		};
#my $res=$data->scrape(URI->new($newuri));
my $res=$data->scrape($mech->response());
for my $i(0 .. $#{$res->{status}})
{
	print $res->{status}[$i];
	print "\n";
}
