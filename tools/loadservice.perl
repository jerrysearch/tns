#!/usr/bin/perl
# This Perl script open connection and call domains
# $jar stands for path of jmxterm jar file
use strict;

my @array;
my $file = "service.cfg";

open (my $fh,'<:encoding(UTF-8)', $file) or die "Can't open the file $file: ";

my $pid=`ps -ef | grep -e ThriftNameServer | grep -v grep | awk '{print \$2}'`;
if($pid <= 0){
print "Can't open the pid [thriftnameserver]\n";
exit;
}
open JMX, "| java -jar jmxterm-1.0-alpha-4-uber.jar -n";
print JMX "open $pid\n";
print JMX "bean -d SNodeManagerMBean name=sNodeManagerMBean\n";
while (my $line =<$fh>)
{
    chomp ($line);
    @array = split(" ", $line);
    if( @array == 4 ){
     my($key1, $key2, $key3, $key4) = @array;
     my $tmp = index $key1, '#';
     if( $tmp != 0 ){
   	print JMX "run onLine $key1 $key2 $key3 $key4\n";
     }
    }
}
close $fh;

print JMX "close\n";
print JMX "bye\n";
close JMX;