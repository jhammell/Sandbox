#!/usr/bin/perl -w
############################################################################
#
#   This is a wrapper for the USGS Alkalinity Calculator.
#   This will accept certain command line parameters and then call the
#   calculate method.
#
############################################################################

# Turn on for debugging only.  Not needed for routine work.
#use strict;
#use warnings;
use diagnostics;


# Load the calculation subroutines.
require "gran_calculations.pl" or die "unable to load calculation routines\n";

our ($site_name, $site_id,
     $collect_date, $collect_time, $temp_input, $spcond_input,
     $analyst, $analysis_date, $analysis_time,
     $volume, $filtered, $specify_conc, $acid_conc,
     $lotnumber, $cor_factor, $expiration, $stir_method,
     $units, $data_order, $data_text, $comments_text,
     $inc_method, $gran_method, $fit_method, $fixed_method,
     $bicarb_fixed,  $carb_fixed, 
     $bicarb_endpt2, $carb_endpt2, 
     $psprint, $ps_tname, 
     $value_mgL, $value_meqL, $valueb_mgL, $valueb_meqL,
     $alk6_str, $alk6_meq_str
    );

my ($data_string,$sample_type,$result);

# Initialize the final values to 0.
# $value_mgL = $value_meqL = $valueb_mgL = $valueb_meqL = 0.0;

# Set some defaults which will always be the same when this calculator is used.
$units = "digital";
$data_order = "vol_first";
$inc_method = 0;
$fit_method = 0;
$fixed_method = 0;
$gran_method = 1;
$cor_factor = 1.01;
$comments_text = "NEON";
$psprint = 1;
$filtered = "yes";

# Now set variables based on the input for this particular sample.
#$temp_input = 19.2;
#$spcond_input = 1013;
#$volume = 100;
#$acid_conc = 1.6;
#$data_string = "0,8.26;3,8.22;6,8.15;9,8.05;13,7.88;16,7.76;19,7.66;22,7.59;25,7.53;28,7.47;31,7.4;34,7.36;37,7.31;40,7.27;50,7.13;60,7.05;81,6.86;124,6.59;155,6.44;205,6.18;255,5.95;295,5.7;315,5.55;325,5.45;329,5.42;333,5.38;337,5.34;341,5.29;344,5.23;347,5.21;350,5.17;353,5.1;356,5.05;359,4.97;362,4.91;365,4.84;368,4.76;371,4.64;375,4.5;378,4.34;381,4.19;384,4.05;387,3.95;390,3.83;393,3.74;396,3.65;408,3.41;428,3.18;";

($temp_input, $volume, $spcond_input, $acid_conc, $data_string) = @ARGV;
print "Input values:\n";
print "\n temp: " . $temp_input;
print "\n vol:  " . $volume;
print "\n cond: " . $spcond_input;
print "\n norm: " . $acid_conc;
print "\n data: " . $data_string;

# Convert the ';' pair separator to a ','. The USGS calculator code
# expects just a ',' separator, which it will remove anyway.
$data_string =~ s/;/,/g;

$data_text = $data_string; 
print "\nCalling the calculate method...\n";
$result = &calculate;

if (defined $result and length $result) {
    if ($result >= 0) {
        print "Call to calculate was successful \n";
    }
}

if (defined $value_meqL) {
    print "meqL:" . $value_meqL . "\n";
}
elsif (defined $valueb_meqL) {
    print "meqL:" . $valueb_meqL . "\n";
}

if (defined $value_mgL) {
    print "mgL:" . $value_mgL . "\n";
}
elsif (defined $valueb_mgL) {
    print "mgL:" . $valueb_mgL . "\n";
}

exit;

