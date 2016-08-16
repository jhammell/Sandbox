############################################################################
#
#  The Alkalinity Calculator
#  Perl/Tk Interface, Calculation Routines
#  Copyright (c) 2003-2012, Stewart A. Rounds
#
#  Contact:
#    Stewart A. Rounds
#    U.S. Geological Survey
#    2130 SW 5th Avenue
#    Portland, OR 97201
#    sarounds@usgs.gov
#  (I work for the U.S. Geological Survey, but this program was
#   developed on my own time.)
#
#  This program is free software; you may redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software Foundation,
#  Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#
############################################################################

# Turn on for debugging only.  Not needed for routine work.
#use strict;
#use warnings;
use diagnostics;

#
# Declare the usage of some global variables.
#
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

#
# Claim some variables as local to this file.
#
my ($highest_ph, %data,
    $F1, $F2, $F3, $K1, $K2, $Kw, $I_str, $gamma_H,
    $bicarb_meq, $carb_meq, $oh_meq, $counts_per_mL,
    $ph_fmt, $meq_fmt, $vol_fmt, $vol_string,
    $do_bicarb, $do_carb,
    $bicarb_ph_upper, $bicarb_ph_lower,
    $carb_ph_upper,   $carb_ph_lower,
    @pcom, @xicom,
   );


sub calculate {
#
# Claim some variables as local to this subroutine.
#
my ($result_pane, $data_frame, $data_row, $table_frame, $trow, $row,
    $mg_fmt, $vol_units, $warn_vol, $warn_deleted,
    $data, $comments, $alk_meq, $alk_label, $ph_inc, $dph, $dcounts,
    $temp, $temp_str, $temp_assumed, $spcond, $spcond_str, $spcond_assumed,
    $cor_factor_str, $cor_factor_assumed, $last_ph, $last_count, $last_vol,
    $slope, $ph, $lowest_ph, $max_slope, $n, $start_ph,
    $view, $canvas, $xaxis, $yaxis, $yaxis2, $ycor, $y, $maxy, $vol, $notes,
   );

my (@data, %backwards, %slope, @xydata, @slopedata,
    $log10Kw, $log10K1, $log10K2, $ph_split,
   );

my ($Method1carb_fail,
    $carb_endpt1,       $carb_endpt1_vol,       $carb_endpt1b_vol,
    $carb_endpt1_str,   $carb_endpt1_vol_str,   $found_carb_tie,
    $bicarb_endpt1,     $bicarb_endpt1_vol,     $bicarb_endpt1b_vol,
    $bicarb_endpt1_str, $bicarb_endpt1_vol_str, $found_bicarb_tie,
    $alk1,     $alk1_str,     $alk1_meq_str,
    $bicarb1,  $bicarb1_str,  $bicarb1_meq_str,
    $carb1,    $carb1_str,    $carb1_meq_str,
    $oh,       $oh_str,       $oh_meq_str,
   );

my ($Method2carb_fail,
    $carb_endpt2_vol,   $carb_endpt2_str,   $carb_endpt2_vol_str,
    $bicarb_endpt2_vol, $bicarb_endpt2_str, $bicarb_endpt2_vol_str,
    $alk2,     $alk2_str,     $alk2_meq_str,
    $bicarb2,  $bicarb2_str,  $bicarb2_meq_str,
    $carb2,    $carb2_str,    $carb2_meq_str,
   );

my ($GranF1_start, $GranF2_start, $GranF3_start, $GranF4_start, $GranF5_start,
    $GranF2_end, $GranF3_end, $GranF4_end, $GranF6_end, $H, $use_defaults,
    $carb_endpt, $carb_endpt_H, $bicarb_endpt, $bicarb_endpt_H,
   );

my ($i, $iter, $ftol, $fret,
    $Ct4, $alk4, $Ct3, $alk3, $alk3_tmp, $alk3_str, $alk3_meq_str,
    $carb_endpt4,       $carb_endpt4_vol,
    $carb_endpt4_str,   $carb_endpt4_vol_str,
    $bicarb_endpt3,     $bicarb_endpt3_vol,
    $bicarb_endpt3_str, $bicarb_endpt3_vol_str,
    $bicarb3,  $bicarb3_str,  $bicarb3_meq_str,
    $carb3,    $carb3_str,    $carb3_meq_str,
   );

my ($Ct5, $alk5, $alk5_tmp, $alk5_str, $alk5_meq_str,
    $carb_endpt5,       $carb_endpt5_vol,
    $carb_endpt5_str,   $carb_endpt5_vol_str,
    $bicarb_endpt5,     $bicarb_endpt5_vol,
    $bicarb_endpt5_str, $bicarb_endpt5_vol_str,
    $bicarb5,  $bicarb5_str,  $bicarb5_meq_str,
    $carb5,    $carb5_str,    $carb5_meq_str,
   );

my (@Gran, @reg, $GrF1, $GrF2, $GrF3, $GrF4, $GrF5, $GrF6,
    $alk6, $alk6b, $alk6b_str, $alk6b_meq_str,
    $Ct6, $oh_endpt_vol, $oh_endpt_ph,
    $oh_endpt6_vol,         $oh_endpt6b_vol,
    $oh_endpt6_vol_str,     $oh_endpt6b_vol_str,
    $carb_endpt6_vol,       $carb_endpt6b_vol,
    $carb_endpt6_vol_str,   $carb_endpt6b_vol_str,
    $bicarb_endpt6_vol,     $bicarb_endpt6b_vol,
    $bicarb_endpt6_vol_str, $bicarb_endpt6b_vol_str,
    $bicarb6,  $bicarb6_str,  $bicarb6_meq_str,
    $carb6,    $carb6_str,    $carb6_meq_str,
    $bicarb6b, $bicarb6b_str, $bicarb6b_meq_str,
    $carb6b,   $carb6b_str,   $carb6b_meq_str,
    $newbicarb_endpt6b_vol, $newcarb_endpt6_vol, $rspan,
   );

my ($check_str1, $check_str2, $check_str3,
    $check_str5, $check_str6, $check_str6b,
   );

my ($Method3_success, %vol3, %calc_slope3, @xyfit3, @slopefit3,
    $Method4_success, %vol4, %calc_slope4, @xyfit4, @slopefit4,
    $Method5_success, %vol5, %calc_slope5, @xyfit5, @slopefit5,
   );

my ($GranF1_success, $GranF1_pts, $GranF1_n, $GranF1_slope, $GranF1_int,
    $GranF2_success, $GranF2_pts, $GranF2_n, $GranF2_slope, $GranF2_int,
    $GranF3_success, $GranF3_pts, $GranF3_n, $GranF3_slope, $GranF3_int,
    $GranF4_success, $GranF4_pts, $GranF4_n, $GranF4_slope, $GranF4_int,
    $GranF5_success, $GranF5_pts, $GranF5_n, $GranF5_slope, $GranF5_int,
    $GranF6_success, $GranF6_pts, $GranF6_n, $GranF6_slope, $GranF6_int,
    %newGranF2, $newGranF2_n, $newGranF2_slope, $newGranF2_int,
    %newGranF3, $newGranF3_n, $newGranF3_slope, $newGranF3_int,
    %GranF1, %GranF2, %GranF3, %GranF4, %GranF5, %GranF6,
    @F1data, @F2data, @F3data, @F4data, @F5data, @F6data,
   );


#
# Set some constants.
#
$carb_meq   = 60009.2;    # MW(CO3)   * 1000
$bicarb_meq = 61017.1;    # MW(HCO3)  * 1000
$alk_meq    = 50043.6;    # MW(CaCO3) * 1000 / 2
$oh_meq     = 17007.3;    # MW(OH)    * 1000

#
# Extract data from the text input widgets.
# Lop off leading and trailing whitespace.
#
#todo: $data     = $data_text->get('1.0', 'end');
$data     = $data_text;
$data     =~ s/^\s+//;
$data     =~ s/\s+$//;
#$comments = $comments_text->get('1.0', 'end');
$comments = $comments_text;
$comments =~ s/^\s+//;
$comments =~ s/\s+$//;

#
# Set the Alk/ANC string.
#
if ($filtered eq "yes") {
    $alk_label = "Alkalinity:";
} else {
    $alk_label = "ANC:";
}

#
# Consider the data units.  Set some formats.
#
if ($units eq "digital") {
    $vol_units = "Counts";
    $vol_string = "counts";
    $vol_fmt = "%.1f ";
    $counts_per_mL = 800.0;
} else {
    $vol_units = "Volume";
    $vol_string = "mL";
    $vol_fmt = "%.2f ";
    $counts_per_mL = 1.0;
}
$ph_fmt  = "%.2f";
$mg_fmt  = "%.1f";
$meq_fmt = "%.2f";

#
# Assume a temperature and specific conductance if none given.
# Temp in deg C, specific conductance in uS/cm.
#
$temp = $temp_input;
$temp =~ s/[^+-.eE\d]//g;
$temp_assumed = "";
if ($temp eq "") {
    $temp = 20.0;
    $temp_assumed = " (assumed)";
}
$temp_str = sprintf("%.2f",$temp);

$spcond = $spcond_input;
$spcond =~ s/[^+-.eE\d]//g;
$spcond_assumed = "";
if ($spcond eq "") {
    $spcond = 50.0;
    $spcond_assumed = " (assumed)";
}
$spcond_str = sprintf("%.1f",$spcond);


#
# Exit if no volume data given.
#
$volume =~ s/[^+-.eE\d]//g;
if ($volume eq "" or $volume <= 0) {
    warn "No volume specified.";
    return -1;
}

#
# Exit if no acid concentration given.
#
if ($acid_conc eq "other") {
    $specify_conc =~ s/[^+-.eE\d]//g;
    if ($specify_conc > 0) {
        $acid_conc = $specify_conc;
    } else {
        warn "Mornality not specified.";
        return -1;
    }
}

#
# Exit if the acid correction factor is out of bounds.
# Assume a correction factor of 1.0 if none is given.
#
$cor_factor =~ s/[^+-.eE\d]//g;
$cor_factor_assumed = "";
if ($cor_factor eq "") {
    $cor_factor = 1.0;
    $cor_factor_assumed = " (assumed)";
} elsif ($cor_factor < 0.8 || $cor_factor > 1.2) {
    warn "Invalid correction factor specified ($cor_factor)";
    return -1;
}
$cor_factor_str = sprintf("%.3f",$cor_factor) . $cor_factor_assumed
                . " [<a href=\"cor_factor.html\">help</a>]";

#
# Use the acid correction factor and set the F factors
#
$F1 = $carb_meq   * $acid_conc * $cor_factor / $counts_per_mL;
$F2 = $bicarb_meq * $acid_conc * $cor_factor / $counts_per_mL;
$F3 = $alk_meq    * $acid_conc * $cor_factor / $counts_per_mL;

#
# Exit if no titration data given.
#
if ($data eq "") {
    warn "No titration data entered.";
    return -1;
}

#
# Remove any commas, retain only numbers (+-.eE\d\s),
#  split the fields, and put the data into a hash
#
$data =~ s/,/ /g;
$data =~ s/[^+-.eE\d\s]//g;
@data = split(/\s+/, $data);
%data = @data;

#
# Reverse the hash if the user passed the titrant volume 
# in the first column
#
if ($data_order eq "vol_first") {
    %backwards = reverse %data;
    %data = %backwards;
}

$warn_vol = $warn_deleted = 0;
$data_row = 0;
foreach $ph ( reverse sort numerically keys(%data) ) {
    $data_row++;
    if ($data_row == 1) {
        $highest_ph = $ph;
        $last_ph = $ph;
        $last_count = $data{$ph};

    } elsif (abs($data{$ph} - $last_count) != 0.0) {
        $ph_inc = $last_ph - $ph;
        $dph = sprintf($ph_fmt, $ph_inc);
        $dcounts = sprintf($vol_fmt, $data{$ph} - $last_count);
        $slope{$ph} = $ph_inc / ($data{$ph} - $last_count);
        $slope = sprintf("%.6f",$slope{$ph});
        $slope{$ph} = $slope;   #added 3/14/00 to prevent rounding problems
        $warn_vol   = 1 if ($data{$ph} < $last_count);
        $last_ph    = $ph;
        $last_count = $data{$ph};
        $lowest_ph  = $ph;

    } else {
        $data_row--;
        delete $data{$ph};
        $warn_deleted = 1;
    }
}

if ($warn_vol) {
    warn "Warning -- One or more of your data points had an unexpected " .
      "titrant volume.  This program expects the pH to decrease " .
      "sequentially as more titrant volume is added.  " .
      "Please check your input for typographical errors.";
}

if ($warn_deleted) {
    warn "Warning -- One or more of your data points was deleted because " .
      "the titrant volume was identical to another volume.  " .
      "Please check your input for typographical errors.";
}

if ($highest_ph > 15 || $lowest_ph < -1) {
    warn "Error -- The range of your pH data exceeds normal expectations.  " .
      "This program expects pH values between -1 and 15.  " .
      "Please check your input for typographical errors.";
    return -1;
}

if ($data{$highest_ph} != 0.0) {
    warn "Error -- This program requires that you provide the initial pH " .
      "of your sample (the sample pH where the titrant volume " .
      "is zero).  Please go back and add that data point.";
    return -1;
}

if ($data_row == 1) {
    warn "Error -- A titration curve is made up of more than just one " .
      "data point.  Please go back and enter data from the entire " .
      "titration curve.";
    return -1;
}

$xaxis = &set_axis_specs(0, $data{$lowest_ph});

$yaxis = { min        => int($lowest_ph),
           max        => int($highest_ph +1.0),
           major      => 0.5,
           minorticks => 4,
           prec       => 1,
           ticklabels => 1,
           label      => "pH",
           color      => "red",
         };
$yaxis->{major} = 1.0 if ($yaxis->{max} - $yaxis->{min} > 5.0);

#
# Graph the measured titration data and the measured slope data.
#
@xydata    = ();
@slopedata = ();
$max_slope = 0;
foreach $ph ( reverse sort numerically keys(%data) ) {
    push (@xydata, $data{$ph}, $ph);
    if ($ph < $highest_ph) {
        push (@slopedata, ($data{$ph} +$last_vol)/2.0, $slope{$ph});
        if ($max_slope < $slope{$ph}) { $max_slope = $slope{$ph}; }
    }
    $last_vol = $data{$ph};
}

$yaxis2 = &set_axis_specs(0, $max_slope);

################################################################################
#
# Do a few tests on the specified analysis methods.
#
# Exit if no analysis method was specified.
#
if (! $inc_method   && ! $fit_method &&
    ! $fixed_method && ! $gran_method) {
    warn "You must specify a titration analysis method. " .
        "Please go back and check one or more of " .
        "the boxes next to the various analysis methods.";
    return -1;
}

################################################################################
#
# Get the equilibrium constants.
#
&get_constants($temp, $spcond);

$log10Kw  = sprintf("%.2f", (log($Kw) / log(10.0)));
$log10K1  = sprintf("%.2f", (log($K1) / log(10.0)));
$log10K2  = sprintf("%.2f", (log($K2) / log(10.0)));
$ph_split = -1.0 * $log10K1;


################################################################################
#
# Find the endpoints.  Calculate the concentrations of CO3, HCO3, Alk.
#
# Method 1:  inflection point method, using measured titration slope data
# Method 2:  user-specified fixed endpoint (pretty easy, eh?)
# Method 3:  theoretical carbonate titration curve method on bicarbonate endpoint
# Method 4:  theoretical carbonate titration curve method on carbonate endpoint
# Method 5:  theoretical carbonate titration curve method on all data points
# Method 6:  Gran's method
#
# Method 1 always is done, but not necessarily reported.
#


################################################################################
#
# Method 1 for carbonate - inflection point method
#
#   Trap for condition with no data in carbonate inflection point region.
#   Set the first point as a dummy endpoint.
#

$Method1carb_fail = 0;
if ($highest_ph > 8.3) {
    $max_slope = $n = 0;
    $carb_endpt1_vol = $data{$highest_ph};
    foreach $ph ( reverse sort numerically keys(%data) ) {
        last if ($ph <= $ph_split);
        if ($ph < $highest_ph && $ph < abs($log10K2)) {
            if ($slope{$ph} > $max_slope) {
                $max_slope = $slope{$ph};
                $carb_endpt1_vol = ($data{$ph} + $last_vol)/2.0;
                $found_carb_tie = 0;
            } elsif ($slope{$ph} == $max_slope) {
                $carb_endpt1b_vol = ($data{$ph} + $last_vol)/2.0;
                $found_carb_tie = 1;
            }
            $n++;
        }
        $last_vol = $data{$ph};
    }

#
#   If two or more points have the same maximum slope,
#   set the endpoint by averaging the volumes.
#
    if ($n > 0) {
        if ($found_carb_tie) {
            $carb_endpt1_vol = 0.5 * ($carb_endpt1_vol + $carb_endpt1b_vol);
        }

#
#   Find endpoint pH by interpolation.
#
        $carb_endpt1 = $last_ph = $highest_ph;
        $last_vol = $data{$highest_ph};
        foreach $ph ( reverse sort numerically keys(%data) ) {
            if ($data{$ph} >= $carb_endpt1_vol && $last_vol < $carb_endpt1_vol) {
                $carb_endpt1 = $last_ph + ($ph - $last_ph)
                       *($carb_endpt1_vol - $last_vol) /($data{$ph} - $last_vol);
                last;
            }
            $last_vol = $data{$ph};
            $last_ph  = $ph;
        }

        $carb_endpt1_str = sprintf("pH $ph_fmt", $carb_endpt1);
        $carb_endpt1_vol_str = sprintf($vol_fmt, $carb_endpt1_vol) . $vol_string;

    } else {
        $carb_endpt1_str     = "insufficient data";
        $carb_endpt1_vol_str = "insufficient data";
        $carb_endpt1_vol     = 0;
        $Method1carb_fail    = 1;
    }

} else {
    $carb_endpt1_str     = "N/A";
    $carb_endpt1_vol_str = "N/A";
    $carb_endpt1_vol     = 0;
}


################################################################################
#
# Method 1 for bicarbonate - inflection point method
#
#   Trap for condition with no data in bicarbonate inflection point region.
#   Set the first point as a dummy endpoint.
#
$max_slope = $n = 0;
$bicarb_endpt1_vol = $data{$highest_ph};
foreach $ph ( reverse sort numerically keys(%data) ) {
    if ($ph < $highest_ph && $ph <= $ph_split) {
        if ($slope{$ph} > $max_slope) {
            $max_slope = $slope{$ph};
            $bicarb_endpt1_vol = ($data{$ph} + $last_vol)/2.0;
            $found_bicarb_tie = 0;
        } elsif ($slope{$ph} == $max_slope) {
            $bicarb_endpt1b_vol = ($data{$ph} + $last_vol)/2.0;
            $found_bicarb_tie = 1;
        }
        $n++;
    }
    $last_vol = $data{$ph};
}

#
# If two or more points have the same maximum slope,
# set the endpoint by averaging the volumes.
#
if ($n > 0) {
    if ($found_bicarb_tie) {
        $bicarb_endpt1_vol = 0.5 * ($bicarb_endpt1_vol + $bicarb_endpt1b_vol);
    }

#
# Find endpoint pH by interpolation.
#
    $bicarb_endpt1 = $last_ph = $highest_ph;
    $last_vol = $data{$highest_ph};
    foreach $ph ( reverse sort numerically keys(%data) ) {
        if ($data{$ph} >= $bicarb_endpt1_vol && $last_vol < $bicarb_endpt1_vol) {
            $bicarb_endpt1 = $last_ph + ($ph - $last_ph)
                    *($bicarb_endpt1_vol - $last_vol) /($data{$ph} - $last_vol);
            last;
        }
        $last_vol = $data{$ph};
        $last_ph  = $ph;
    }

    $bicarb_endpt1_str = sprintf("pH $ph_fmt", $bicarb_endpt1);
    $bicarb_endpt1_vol_str = sprintf($vol_fmt, $bicarb_endpt1_vol) . $vol_string;
    $alk1 = $bicarb_endpt1_vol * $F3 / $volume;
    $alk1_str = sprintf($mg_fmt, $alk1) . " mg/L as CaCO<sub>3</sub>";
    $alk1_meq_str = sprintf($meq_fmt, $alk1 * 1000./$alk_meq) . " meq/L";

#
# End up here if no data are available at or below pK1 for use in
# finding the bicarbonate inflection point.  Calculate a dummy
# alkalinity just in case the user is trying the Gran or theoretical
# fit method.  The inflection and fixed endpoint methods should
# have been disallowed earlier, but disable them here to be sure.
#

} else {
    $bicarb_endpt1_vol     = $data{$lowest_ph};
    $alk1                  = $bicarb_endpt1_vol * $F3 / $volume;
    $bicarb_endpt1_str     = "insufficient data";
    $bicarb_endpt1_vol_str = "insufficient data";
    $alk1_str              = "insufficient data";
    $alk1_meq_str          = "insufficient data";
    $inc_method = $fixed_method = 0;               # failsafe
}

#
# Calculate speciation.
#
($bicarb1, $carb1, $oh) = &get_speciation($alk1/$alk_meq);

$bicarb1_str = sprintf($mg_fmt, $bicarb1) . " mg/L as HCO<sub>3</sub><sup>-</sup>";
$carb1_str   = sprintf($mg_fmt, $carb1) . " mg/L as CO<sub>3</sub><sup>2-</sup>";
$oh_str      = sprintf($mg_fmt, $oh) . " mg/L as OH<sup>-</sup>";

$bicarb1_meq_str = sprintf($meq_fmt, $bicarb1 *1000./$bicarb_meq) . " meq/L";
$carb1_meq_str   = sprintf($meq_fmt, $carb1 *2000./$carb_meq) . " meq/L";
$oh_meq_str      = sprintf($meq_fmt, $oh *1000./$oh_meq) . " meq/L";

#
# Check for inconsistencies.
#
$check_str1 = &get_criteria($alk1/$alk_meq,
                  $bicarb_endpt1_vol, $carb_endpt1_vol, 0);


################################################################################
#
# Set pH regions for theoretical CTC and Gran methods.  This is done after the
# inflection point method because it uses results from that method.
#
# Set defaults.
#

if ($fit_method || $gran_method) {
    $carb_ph_upper   = -0.5 *($log10K1 + $log10K2) + 1.0;
    $carb_ph_lower   = -0.5 *($log10K1 + $log10K2) - 1.0;
    $bicarb_ph_upper = -1.0 * $log10K1 - 0.6;
    $bicarb_ph_lower = $bicarb_ph_upper - 2.5;

    $GranF1_start = 4.15;                                # near pH=4.15
    $GranF2_start = -0.5 *($log10K1 + $log10K2) - 0.35;  # near pH=7.95
    $GranF2_end   = -1.0 * $log10K1 -0.5;                # near pH=5.80
    $GranF3_start = -1.0 * $log10K1 +0.7;                # near pH=7.00
    $GranF3_end   = 4.85;                                # near pH=4.85
    $GranF4_start = 10.3;                                # near pH=10.3
    $GranF4_end   = -0.5 *($log10K1 + $log10K2) + 0.35;  # near pH=8.65

#
# Estimate Ct and use to find better default regions, if possible.
#
    if ($highest_ph > -1.0 * $log10K1 + 0.3) {
        $Ct6 = $bicarb1 /$bicarb_meq + $carb1 /$carb_meq;

    } elsif (abs($data{$highest_ph}) < 0.0001) {
        $alk6 = $alk1 /$alk_meq;
        $H = 10.0 ** (-1.0 * $highest_ph);
        $Ct6 = ($alk6 - $Kw/$H + $H/$gamma_H)
               *($H*$H + $K1*$H + $K1*$K2) /($K1*$H + 2.0*$K1*$K2);
    } else {
        $use_defaults = 1;
    }
    $use_defaults = 1 if ($Ct6 <= 0.0);

    if (! $use_defaults) {
        $bicarb_endpt_H = (-1.0 *$K1 + sqrt($K1 *$K1 + 4.0 * $K1 *$Ct6)) /2.0;
        if ($bicarb_endpt_H > 0.0) {
            $bicarb_endpt = -1.0 * log($bicarb_endpt_H) / log(10.0);
            if ($bicarb_endpt > 1 && $bicarb_endpt <= 7) {
                $bicarb_ph_upper = $bicarb_endpt + 1.2;
                $bicarb_ph_lower = $bicarb_endpt - 1.2;
                $GranF1_start    = $bicarb_endpt - 0.35;
                $GranF3_end      = $bicarb_endpt + 0.35;
            }
        }
        if ($Ct6 < $K1) {
            $carb_ph_upper = 8.0;
            $carb_ph_lower = 6.0;
            $GranF2_start  = 6.65;
            $GranF4_end    = 7.35;
        } elsif ($Ct6 < $Kw / $K2) {
            $carb_endpt_H = ($Kw +sqrt($Kw *$Kw +4.0 *$Ct6 *$Kw *$K1))
                            /(2.0 *$Ct6);
            if ($carb_endpt_H > 0.0) {
                $carb_endpt = -1.0 * log($carb_endpt_H) / log(10.0);
                if ($carb_endpt >= 7 &&
                    $carb_endpt <= -0.5 *($log10K1 + $log10K2)) {
                    $carb_ph_upper = $carb_endpt + 1.0;
                    $carb_ph_lower = $carb_endpt - 1.0;
                    $GranF2_start  = $carb_endpt - 0.35;
                    $GranF4_end    = $carb_endpt + 0.35;
                }
            }
        }
    }
}


################################################################################
#
# Gran Titration.
#
# Uses the formulae documented in Stumm and Morgan, 1981, 2nd ed., pp 226-229.
# plus F5 and F6, derived by me.
#

if ($gran_method) {  # no indent here

$GranF1_success = $GranF2_success = $GranF3_success = 0;
$GranF4_success = $GranF5_success = $GranF6_success = 0;

#
# Calculate the F1 Gran function.
#
@Gran = ();
@reg = ();
foreach $ph ( reverse sort numerically keys(%data) ) {
    next if ($ph > $ph_split);
    $GrF1 = (($volume + $data{$ph} /$counts_per_mL)
             *10.0 **(-1.0 * $ph)) /$gamma_H;
    push(@Gran, $data{$ph}, $GrF1);
    push(@reg, $data{$ph}, $GrF1) if ($ph <= $GranF1_start);
    $GranF1_pts = 1;
}
%GranF1 = @Gran;

#
# Fit the straight line and calculate the bicarbonate endpoint.
#
($GranF1_n, $GranF1_slope, $GranF1_int) = &regression(@reg);

if ($GranF1_n > 1) {
    $bicarb_endpt6_vol = -1.0 * $GranF1_int / $GranF1_slope;
#   if ($bicarb_endpt6_vol > 0) {
        $GranF1_success = 1;
        $bicarb_endpt6_vol_str = sprintf($vol_fmt, $bicarb_endpt6_vol)
                               . $vol_string;
        $alk6 = $bicarb_endpt6_vol * $F3 / $volume;

        $value_mgL = $alk6;        
        $alk6_str = sprintf($mg_fmt, $alk6) . " mg/L as CaCO<sub>3</sub>";
        $value_meqL = $alk6 *1000./$alk_meq;
        $alk6_meq_str = sprintf($meq_fmt, $alk6 *1000./$alk_meq) . " meq/L";

#
# Calculate speciation from F1 result.
#
        ($bicarb6, $carb6) = &get_speciation($alk6/$alk_meq);

        $bicarb6_str = sprintf($mg_fmt, $bicarb6)
                       . " mg/L as HCO<sub>3</sub><sup>-</sup>";
        $carb6_str   = sprintf($mg_fmt, $carb6)
                       . " mg/L as CO<sub>3</sub><sup>2-</sup>";

        $bicarb6_meq_str = sprintf($meq_fmt, $bicarb6 *1000./$bicarb_meq) . " meq/L";
        $carb6_meq_str   = sprintf($meq_fmt, $carb6 *2000./$carb_meq) . " meq/L";

#
# Calculate the F2 Gran function, using bicarbonate endpoint from F1.
#
        @Gran = ();
        @reg = ();
        foreach $ph ( reverse sort numerically keys(%data) ) {
            last if ($ph < $GranF2_end);
            $GrF2 = ($bicarb_endpt6_vol - $data{$ph}) /$counts_per_mL
                     *10.0 **(-1.0 * $ph);
            push(@Gran, $data{$ph}, $GrF2);
            push(@reg, $data{$ph}, $GrF2) if ($ph <= $GranF2_start);
            $GranF2_pts = 1;
        }
        %GranF2 = @Gran;

#
# Fit a straight line to F2 and calculate the carbonate endpoint.
#
        ($GranF2_n, $GranF2_slope, $GranF2_int) = &regression(@reg);

        if ($GranF2_n > 1) {
            $carb_endpt6_vol = -1.0 * $GranF2_int / $GranF2_slope;
            if ($carb_endpt6_vol > 0) {
                $GranF2_success = 1;
                $carb_endpt6_vol_str = sprintf($vol_fmt, $carb_endpt6_vol)
                                     . $vol_string;
            } else {
                $carb_endpt6_vol_str  = "N/A";
                $carb_endpt6b_vol_str = "N/A";
                $oh_endpt6_vol_str    = "N/A";
                $oh_endpt6b_vol_str   = "N/A";
                $carb_endpt6_vol      = 0;
            }
        } else {
            $carb_endpt6_vol_str  = "insufficient data";
            $carb_endpt6b_vol_str = "insufficient data";
            $oh_endpt6_vol_str    = "insufficient data";
            $oh_endpt6b_vol_str   = "insufficient data";
            $carb_endpt6_vol      = 0;
        }

#
# Check for inconsistencies in results from F1 and F2.
#
        $check_str6 = &get_criteria($alk6/$alk_meq,
                          $bicarb_endpt6_vol, $carb_endpt6_vol, 0);

#
# Calculate the F3 Gran function, even if F2 failed.
#
        @Gran = ();
        @reg = ();
        foreach $ph ( sort numerically keys(%data) ) {
            last if ($ph > $GranF3_start);
            $GrF3 = ($data{$ph} - $carb_endpt6_vol)
                    /$counts_per_mL * 10.0 ** $ph;
            push(@Gran, $data{$ph}, $GrF3);
            push(@reg, $data{$ph}, $GrF3) if ($ph >= $GranF3_end);
            $GranF3_pts = 1;
        }
        %GranF3 = @Gran;

#
# Fit a straight line to F3 and calculate another bicarbonate endpoint.
#
        ($GranF3_n, $GranF3_slope, $GranF3_int) = &regression(@reg);

        if ($GranF3_n > 1) {
            $bicarb_endpt6b_vol = -1.0 * $GranF3_int / $GranF3_slope;
            if ($bicarb_endpt6b_vol > 0) {
                $GranF3_success = 1;
                $bicarb_endpt6b_vol_str = sprintf($vol_fmt, $bicarb_endpt6b_vol)
                                        . $vol_string;
                $alk6b = $bicarb_endpt6b_vol * $F3 / $volume;
                $valueb_mgL = $alk6b;
                $alk6b_str = sprintf($mg_fmt, $alk6b) . " mg/L as CaCO<sub>3</sub>";
                $valueb_meqL = $alk6b *1000./$alk_meq;
                $alk6b_meq_str = sprintf($meq_fmt, $alk6b *1000./$alk_meq) . " meq/L";

#
# Calculate speciation from F3 result.
#
                ($bicarb6b, $carb6b) = &get_speciation($alk6b/$alk_meq);

                $bicarb6b_str = sprintf($mg_fmt, $bicarb6b)
                                . " mg/L as HCO<sub>3</sub><sup>-</sup>";
                $carb6b_str   = sprintf($mg_fmt, $carb6b)
                                . " mg/L as CO<sub>3</sub><sup>2-</sup>";

                $bicarb6b_meq_str = sprintf($meq_fmt, $bicarb6b *1000./$bicarb_meq)
                                    . " meq/L";
                $carb6b_meq_str   = sprintf($meq_fmt, $carb6b *2000./$carb_meq)
                                    . " meq/L";

#
# Check for inconsistencies in results from F3 and F2.
#
                $check_str6b = &get_criteria($alk6b/$alk_meq,
                                  $bicarb_endpt6b_vol, $carb_endpt6_vol, 0);

            } else {
                $bicarb_endpt6b_vol_str = "indeterminate";
                $alk6b_str              = "indeterminate";
                $alk6b_meq_str          = "indeterminate";
                $bicarb6b_str           = "indeterminate";
                $bicarb6b_meq_str       = "indeterminate";
                $carb6b_str             = "indeterminate";
                $carb6b_meq_str         = "indeterminate";
            }

        } else {
            $bicarb_endpt6b_vol_str = "insufficient data";
            $alk6b_str              = "insufficient data";
            $alk6b_meq_str          = "insufficient data";
            $bicarb6b_str           = "insufficient data";
            $bicarb6b_meq_str       = "insufficient data";
            $carb6b_str             = "insufficient data";
            $carb6b_meq_str         = "insufficient data";
        }

#
# Calculate the F4 Gran function.
#  This is done only if F2 succeeds, because we use results from F1 and F2.
#
        if ($GranF2_success) {
            @Gran = ();
            @reg = ();
            foreach $ph ( sort numerically keys(%data) ) {
                next if ($ph < $ph_split);
                last if ($ph > $GranF4_start);
                $GrF4 = ($bicarb_endpt6_vol - 2.0 *$carb_endpt6_vol + $data{$ph})
                        /$counts_per_mL * 10.0 ** $ph;
                push(@Gran, $data{$ph}, $GrF4);
                push(@reg, $data{$ph}, $GrF4) if ($ph >= $GranF4_end);
                $GranF4_pts = 1;
            }
            %GranF4 = @Gran;

#
# Fit a straight line to F4 and calculate another carbonate endpoint.
#
            ($GranF4_n, $GranF4_slope, $GranF4_int) = &regression(@reg);

            if ($GranF4_n > 1) {
                $carb_endpt6b_vol = -1.0 * $GranF4_int / $GranF4_slope;
                if ($carb_endpt6b_vol > 0) {
                    $GranF4_success = 1;
                    $carb_endpt6b_vol_str = sprintf($vol_fmt, $carb_endpt6b_vol)
                                          . $vol_string;
                } else {
                    $carb_endpt6b_vol_str = "N/A";
                }
            } else {
                $carb_endpt6b_vol_str = "insufficient data";
            }

#
# Calculate the F5 Gran function.
#  This is done only if F2 succeeds, because results from F2 are used.
#  The first step is to estimate the OH endpoint, then find pH limits.
#
            $oh_endpt_vol = 2.0 * $carb_endpt6_vol - $bicarb_endpt6_vol;

            if ($oh_endpt_vol > 0.0 &&
                   $carb_endpt6_vol > 0.0 &&
                   $bicarb_endpt6_vol > $carb_endpt6_vol) {

                $last_vol = $data{$highest_ph};
                $oh_endpt_ph = $last_ph = $highest_ph;
                foreach $ph ( reverse sort numerically keys(%data) ) {
                    if ($data{$ph} >= $oh_endpt_vol && $last_vol < $oh_endpt_vol) {
                        $oh_endpt_ph = $last_ph + ($ph - $last_ph)
                               *($oh_endpt_vol - $last_vol) /($data{$ph} - $last_vol);
                        last;
                    }
                    $last_vol = $data{$ph};
                    $last_ph  = $ph;
                }
                $GranF5_start = $oh_endpt_ph - 0.35;
                $GranF6_end   = $oh_endpt_ph + 0.35;

                @Gran = ();
                @reg = ();
                foreach $ph ( reverse sort numerically keys(%data) ) {
                    last if ($ph < $GranF4_end);
                    $GrF5 = ($carb_endpt6_vol - $data{$ph})
                            /$counts_per_mL * 10.0 **(-1.0 * $ph);
                    push(@Gran, $data{$ph}, $GrF5);
                    push(@reg, $data{$ph}, $GrF5) if ($ph <= $GranF5_start);
                    $GranF5_pts = 1;
                }
                %GranF5 = @Gran;

#
# Fit a straight line to F5 and calculate the hydroxide endpoint.
#
                ($GranF5_n, $GranF5_slope, $GranF5_int) = &regression(@reg);

                if ($GranF5_n > 1) {
                    $oh_endpt6_vol = -1.0 * $GranF5_int / $GranF5_slope;
                    if ($oh_endpt6_vol > 0) {
                        if ($oh_endpt6_vol < $carb_endpt6_vol) {
                            $GranF5_success = 1;
                            $oh_endpt6_vol_str = sprintf($vol_fmt, $oh_endpt6_vol)
                                                  . $vol_string;
                        } else {
                            $oh_endpt6_vol_str = "insufficient data";
                        }
                    } else {
                        $oh_endpt6_vol_str = "N/A";
                    }
                } else {
                    $oh_endpt6_vol_str = "insufficient data";
                }

#
# Calculate the F6 Gran function.
#  This is done only if F2 succeeds, because the carbonate endpoint is
#  used to estimate the location of the OH endpoint.
#
                @Gran = ();
                @reg = ();
                foreach $ph ( sort numerically keys(%data) ) {
                    next if ($ph < $GranF4_end);
                    $GrF6 = ($volume + $data{$ph} /$counts_per_mL) * 10.0 ** $ph;
                    push(@Gran, $data{$ph}, $GrF6);
                    push(@reg, $data{$ph}, $GrF6) if ($ph >= $GranF6_end);
                    $GranF6_pts = 1;
                }
                %GranF6 = @Gran;

#
# Fit a straight line to F6 and calculate another hydroxide endpoint.
#
                ($GranF6_n, $GranF6_slope, $GranF6_int) = &regression(@reg);

                if ($GranF6_n > 1) {
                    $oh_endpt6b_vol = -1.0 * $GranF6_int / $GranF6_slope;
                    if ($oh_endpt6b_vol > 0) {
                        if ($oh_endpt6b_vol < $carb_endpt6_vol) {
                            $GranF6_success = 1;
                            $oh_endpt6b_vol_str = sprintf($vol_fmt, $oh_endpt6b_vol)
                                                  . $vol_string;
                        } else {
                            $oh_endpt6b_vol_str = "insufficient data";
                        }
                    } else {
                        $oh_endpt6b_vol_str = "N/A";
                    }
                } else {
                    $oh_endpt6b_vol_str = "insufficient data";
                }

            } else {   # if can't do F5 and F6
                $oh_endpt6_vol_str  = "N/A";
                $oh_endpt6b_vol_str = "N/A";
            }
        }

#
# End up here if too few points to fit Gran function F1
#
} else {
    $bicarb_endpt6_vol_str  = "insufficient data";
    $alk6_str               = "insufficient data";
    $alk6_meq_str           = "insufficient data";
    $bicarb6_str            = "insufficient data";
    $bicarb6_meq_str        = "insufficient data";
    $carb6_str              = "insufficient data";
    $carb6_meq_str          = "insufficient data";

    $bicarb_endpt6b_vol_str = "insufficient data";
    $alk6b_str              = "insufficient data";
    $alk6b_meq_str          = "insufficient data";
    $bicarb6b_str           = "insufficient data";
    $bicarb6b_meq_str       = "insufficient data";
    $carb6b_str             = "insufficient data";
    $carb6b_meq_str         = "insufficient data";

    $carb_endpt6_vol_str    = "insufficient data";
    $carb_endpt6b_vol_str   = "insufficient data";
    $oh_endpt6_vol_str      = "insufficient data";
    $oh_endpt6b_vol_str     = "insufficient data";
}

#
# If F1 failed, try to bypass it with Gran function F3.
#
#if ($GranF1_n < 2 || $bicarb_endpt6_vol <= 0) {
if ($GranF1_n < 2) {
    $carb_endpt6_vol = 0 if (! defined($carb_endpt6_vol));
    @Gran = ();
    @reg = ();
    foreach $ph ( sort numerically keys(%data) ) {
        last if ($ph > $GranF3_start);
        $GrF3 = ($data{$ph} - $carb_endpt6_vol) /$counts_per_mL * 10.0 ** $ph;
        push(@Gran, $data{$ph}, $GrF3);
        push(@reg, $data{$ph}, $GrF3) if ($ph >= $GranF3_end);
        $GranF3_pts = 1;
    }
    %GranF3 = @Gran;

    ($GranF3_n, $GranF3_slope, $GranF3_int) = &regression(@reg);

    if ($GranF3_n > 1) {
        $bicarb_endpt6b_vol = -1.0 * $GranF3_int / $GranF3_slope;
        if ($bicarb_endpt6b_vol > 0) {
            $GranF3_success = 1;
            $bicarb_endpt6b_vol_str = sprintf($vol_fmt, $bicarb_endpt6b_vol)
                                    . $vol_string;
            $alk6b = $bicarb_endpt6b_vol * $F3 / $volume;
            $valueb_mgL = $alk6b;
            $alk6b_str = sprintf($mg_fmt, $alk6b) . " mg/L as CaCO<sub>3</sub>";
            $valueb_meqL = $alk6b *1000./$alk_meq;
            $alk6b_meq_str = sprintf($meq_fmt, $alk6b *1000./$alk_meq) . " meq/L";

#
# Calculate F2, using F3 results.
#
            @Gran = ();
            @reg = ();
            foreach $ph ( reverse sort numerically keys(%data) ) {
                last if ($ph < $GranF2_end);
                $GrF2 = ($bicarb_endpt6b_vol - $data{$ph}) /$counts_per_mL
                         *10.0 **(-1.0 * $ph);
                push(@Gran, $data{$ph}, $GrF2);
                push(@reg, $data{$ph}, $GrF2) if ($ph <= $GranF2_start);
                $GranF2_pts = 1;
            }
            %GranF2 = @Gran;

            ($GranF2_n, $GranF2_slope, $GranF2_int) = &regression(@reg);
            if ($GranF2_n > 1) {
                $carb_endpt6_vol = -1.0 * $GranF2_int / $GranF2_slope;
                if ($carb_endpt6_vol > 0) {
                    $GranF2_success = 1;

#
# Okay, we got a successful F2 from the estimated F3.
# Now, redo F3 using results of F2.
# If it fails, go back to the previous F3 success.
#
                    @Gran = ();
                    @reg = ();
                    foreach $ph ( sort numerically keys(%data) ) {
                        last if ($ph > $GranF3_start);
                        $GrF3 = ($data{$ph} - $carb_endpt6_vol)
                                /$counts_per_mL * 10.0 ** $ph;
                        push(@Gran, $data{$ph}, $GrF3);
                        push(@reg, $data{$ph}, $GrF3) if ($ph >= $GranF3_end);
                    }
                    %newGranF3 = @Gran;

                    ($newGranF3_n, $newGranF3_slope, $newGranF3_int) = &regression(@reg);

                    $newbicarb_endpt6b_vol = -1.0 * $newGranF3_int / $newGranF3_slope;
                    if ($newbicarb_endpt6b_vol > 0) {
                        %GranF3       = %newGranF3;
                        $GranF3_n     = $newGranF3_n;
                        $GranF3_slope = $newGranF3_slope;
                        $GranF3_int   = $newGranF3_int;
                        $bicarb_endpt6b_vol = $newbicarb_endpt6b_vol;

                        $bicarb_endpt6b_vol_str
                            = sprintf($vol_fmt, $bicarb_endpt6b_vol) . $vol_string;
                        $alk6b = $bicarb_endpt6b_vol * $F3 / $volume;
                        $valueb_mgL = $alk6b;
                        $alk6b_str = sprintf($mg_fmt, $alk6b) . " mg/L as CaCO<sub>3</sub>";
                        $valueb_meqL = $alk6b *1000./$alk_meq;
                        $alk6b_meq_str = sprintf($meq_fmt, $alk6b *1000./$alk_meq) . " meq/L";

#
# Now, redo F2, using updated F3 results.
# If it fails, go back to the previous F2 success.
#
                        @Gran = ();
                        @reg = ();
                        foreach $ph ( reverse sort numerically keys(%data) ) {
                            last if ($ph < $GranF2_end);
                            $GrF2 = ($bicarb_endpt6b_vol - $data{$ph}) /$counts_per_mL
                                    *10.0 **(-1.0 * $ph);
                            push(@Gran, $data{$ph}, $GrF2);
                            push(@reg, $data{$ph}, $GrF2) if ($ph <= $GranF2_start);
                        }
                        %newGranF2 = @Gran;

                        ($newGranF2_n, $newGranF2_slope, $newGranF2_int) = &regression(@reg);

                        $newcarb_endpt6_vol = -1.0 * $newGranF2_int / $newGranF2_slope;
                        if ($newcarb_endpt6_vol > 0) {
                            %GranF2       = %newGranF2;
                            $GranF2_n     = $newGranF2_n;
                            $GranF2_slope = $newGranF2_slope;
                            $GranF2_int   = $newGranF2_int;
                            $carb_endpt6_vol = $newcarb_endpt6_vol;
                        }
                    }
                    $carb_endpt6_vol_str = sprintf($vol_fmt, $carb_endpt6_vol)
                                         . $vol_string;

#
# Calculate F4, using results from F2 and F3.
#
                    @Gran = ();
                    @reg = ();
                    foreach $ph ( sort numerically keys(%data) ) {
                        next if ($ph < $ph_split);
                        last if ($ph > $GranF4_start);
                        $GrF4 = ($bicarb_endpt6b_vol - 2.0 *$carb_endpt6_vol + $data{$ph})
                                /$counts_per_mL * 10.0 ** $ph;
                        push(@Gran, $data{$ph}, $GrF4);
                        push(@reg, $data{$ph}, $GrF4) if ($ph >= $GranF4_end);
                        $GranF4_pts = 1;
                    }
                    %GranF4 = @Gran;

                    ($GranF4_n, $GranF4_slope, $GranF4_int) = &regression(@reg);

                    if ($GranF4_n > 1) {
                        $carb_endpt6b_vol = -1.0 * $GranF4_int / $GranF4_slope;
                        if ($carb_endpt6b_vol > 0) {
                            $GranF4_success = 1;
                            $carb_endpt6b_vol_str = sprintf($vol_fmt, $carb_endpt6b_vol)
                                                  . $vol_string;
                        } else {
                            $carb_endpt6b_vol_str = "N/A";
                        }
                    } else {
                        $carb_endpt6b_vol_str = "insufficient data";
                    }

#
# Calculate F5.  This is done only if F2 succeeds.
#
                    $oh_endpt_vol = 2.0 * $carb_endpt6_vol - $bicarb_endpt6b_vol;

                    if ($oh_endpt_vol > 0.0 &&
                           $carb_endpt6_vol > 0.0 &&
                           $bicarb_endpt6b_vol > $carb_endpt6_vol) {

                        $last_vol = $data{$highest_ph};
                        $oh_endpt_ph = $last_ph = $highest_ph;
                        foreach $ph ( reverse sort numerically keys(%data) ) {
                            if ($data{$ph} >= $oh_endpt_vol && $last_vol < $oh_endpt_vol) {
                                $oh_endpt_ph = $last_ph + ($ph - $last_ph)
                                       *($oh_endpt_vol - $last_vol) /($data{$ph} - $last_vol);
                                last;
                            }
                            $last_vol = $data{$ph};
                            $last_ph  = $ph;
                        }
                        $GranF5_start = $oh_endpt_ph - 0.35;
                        $GranF6_end   = $oh_endpt_ph + 0.35;

                        @Gran = ();
                        @reg = ();
                        foreach $ph ( reverse sort numerically keys(%data) ) {
                            last if ($ph < $GranF4_end);
                            $GrF5 = ($carb_endpt6_vol - $data{$ph})
                                    /$counts_per_mL * 10.0 **(-1.0 * $ph);
                            push(@Gran, $data{$ph}, $GrF5);
                            push(@reg, $data{$ph}, $GrF5) if ($ph <= $GranF5_start);
                            $GranF5_pts = 1;
                        }
                        %GranF5 = @Gran;

                        ($GranF5_n, $GranF5_slope, $GranF5_int) = &regression(@reg);

                        if ($GranF5_n > 1) {
                            $oh_endpt6_vol = -1.0 * $GranF5_int / $GranF5_slope;
                            if ($oh_endpt6_vol > 0) {
                                if ($oh_endpt6_vol < $carb_endpt6_vol) {
                                    $GranF5_success = 1;
                                    $oh_endpt6_vol_str = sprintf($vol_fmt, $oh_endpt6_vol)
                                                          . $vol_string;
                                } else {
                                    $oh_endpt6_vol_str = "insufficient data";
                                }
                            } else {
                                $oh_endpt6_vol_str = "N/A";
                            }
                        } else {
                            $oh_endpt6_vol_str = "insufficient data";
                        }

#
# Calculate F6.  This is done only if F2 succeeds.
#
                        @Gran = ();
                        @reg = ();
                        foreach $ph ( sort numerically keys(%data) ) {
                            next if ($ph < $GranF4_end);
                            $GrF6 = ($volume + $data{$ph} /$counts_per_mL) * 10.0 ** $ph;
                            push(@Gran, $data{$ph}, $GrF6);
                            push(@reg, $data{$ph}, $GrF6) if ($ph >= $GranF6_end);
                            $GranF6_pts = 1;
                        }
                        %GranF6 = @Gran;

                        ($GranF6_n, $GranF6_slope, $GranF6_int) = &regression(@reg);

                        if ($GranF6_n > 1) {
                            $oh_endpt6b_vol = -1.0 * $GranF6_int / $GranF6_slope;
                            if ($oh_endpt6b_vol > 0) {
                                if ($oh_endpt6b_vol < $carb_endpt6_vol) {
                                    $GranF6_success = 1;
                                    $oh_endpt6b_vol_str = sprintf($vol_fmt, $oh_endpt6b_vol)
                                                          . $vol_string;
                                } else {
                                    $oh_endpt6b_vol_str = "insufficient data";
                                }
                            } else {
                                $oh_endpt6b_vol_str = "N/A";
                            }
                        } else {
                            $oh_endpt6b_vol_str = "insufficient data";
                        }

                    } else {   # if can't do F5 and F6
                        $oh_endpt6_vol_str  = "N/A";
                        $oh_endpt6b_vol_str = "N/A";
                    }

#
# End up here if Gran function F2 gives negative endpoint
#
                } else {
                    warn "Gran F2 has negative endpoints...\n";
                    $carb_endpt6_vol_str  = "N/A";
                    $carb_endpt6b_vol_str = "N/A";
                    $oh_endpt6_vol_str    = "N/A";
                    $oh_endpt6b_vol_str   = "N/A";
                    $carb_endpt6_vol      = 0;
                }

#
# End up here if too few points to fit Gran function F2
#
            } else {
                warn "Too few points for Gran F2...\n";
                $carb_endpt6_vol_str  = "insufficient data";
                $carb_endpt6b_vol_str = "insufficient data";
                $oh_endpt6_vol_str    = "insufficient data";
                $oh_endpt6b_vol_str   = "insufficient data";
                $carb_endpt6_vol      = 0;
            }

#
# Calculate speciation from F3 result.
#
            ($bicarb6b, $carb6b) = &get_speciation($alk6b/$alk_meq);

            $bicarb6b_str = sprintf($mg_fmt, $bicarb6b)
                            . " mg/L as HCO<sub>3</sub><sup>-</sup>";
            $carb6b_str   = sprintf($mg_fmt, $carb6b)
                            . " mg/L as CO<sub>3</sub><sup>2-</sup>";

            $bicarb6b_meq_str = sprintf($meq_fmt, $bicarb6b *1000./$bicarb_meq)
                                . " meq/L";
            $carb6b_meq_str   = sprintf($meq_fmt, $carb6b *2000./$carb_meq)
                                . " meq/L";

#
# Check for inconsistencies in results from F3 and F2.
#
            $check_str6b = &get_criteria($alk6b/$alk_meq,
                              $bicarb_endpt6b_vol, $carb_endpt6_vol, 0);

#
# End up here if Gran function F3 gives negative endpoint
#
        } else {
            warn "F3 has negative endpoint.\n";
            $bicarb_endpt6b_vol_str = "indeterminate";
            $alk6b_str              = "indeterminate";
            $alk6b_meq_str          = "indeterminate";
            $bicarb6b_str           = "indeterminate";
            $bicarb6b_meq_str       = "indeterminate";
            $carb6b_str             = "indeterminate";
            $carb6b_meq_str         = "indeterminate";

            $carb_endpt6_vol_str    = "indeterminate";
            $carb_endpt6b_vol_str   = "indeterminate";
            $oh_endpt6_vol_str      = "indeterminate";
            $oh_endpt6b_vol_str     = "indeterminate";
        }

#
# End up here if too few points to fit Gran function F3
#
    } else {
        warn "Too few points to fit F3.\n";
        $bicarb_endpt6b_vol_str = "insufficient data";
        $alk6b_str              = "insufficient data";
        $alk6b_meq_str          = "insufficient data";
        $bicarb6b_str           = "insufficient data";
        $bicarb6b_meq_str       = "insufficient data";
        $carb6b_str             = "insufficient data";
        $carb6b_meq_str         = "insufficient data";

        $carb_endpt6_vol_str    = "insufficient data";
        $carb_endpt6b_vol_str   = "insufficient data";
        $oh_endpt6_vol_str      = "insufficient data";
        $oh_endpt6b_vol_str     = "insufficient data";
    }
}
}  # from if ($gran_method) -- no indent

return 1;

} # sub calculate


################################################################################
#
# Calculate the values of the equilibrium constants.
#  This is a temperature adjustment AND an activity correction.
#  Activity corrections are estimated based on the sample's specific
#  conductance, input by the user, and correlated to dissolved solids
#  concentration.
#
# These calculations taken from the water-quality model CE-QUAL-W2, ver. 2.0
# The correlation between specific conductance and dissolved solids was
#  taken from Hem (1985, USGS WSP 2254, p. 67, 3rd ed.).
#
# Specific conductance in uS/cm.  Dissolved solids in mg/L.
# Ionic strength in eq per liter.
#
# Kw, K1, K2, I_str, gamma_H are all global variables needed elsewhere.
#
sub get_constants {
    my($T, $spc) = @_;
    my($ds, $I, $sqrtI, $dh1, $dh2);
    my($gamma_H2CO3, $gamma_HCO3, $gamma_CO3, $gamma_OH);

    $T += 273.15;
    $ds = 0.59 * $spc;

    $I = 0.000025 * $ds;
    $I_str = sprintf("%.2e",$I);
    $sqrtI = sqrt($I);

#
# Debye-Huckel terms and activity coefficients
#
    $dh1 = -0.5085 * $sqrtI /(1.0 +1.3124 *$sqrtI)
           +0.004745694 +0.04160762 *$I -0.009284843 *$I *$I;
    $dh2 = -2.0340 * $sqrtI /(1.0 +1.4765 *$sqrtI)
           +0.01205665  +0.09715745 *$I -0.02067746  *$I *$I;

    $gamma_H2CO3 = 10.0 **(0.0755 *$I);
    $gamma_HCO3  = 10.0 ** $dh1;
    $gamma_CO3   = 10.0 ** $dh2;
    $gamma_OH    = $gamma_HCO3;
    $gamma_H     = $gamma_HCO3;

#
#   Calculate the mixed equilibrium constants.
#   Equations from Stumm & Morgan's "Aquatic Chemistry," 3rd edition.
    $Kw = (10.0 **(-283.9710 -0.05069842*$T +13323.00/$T +102.24447*log($T)/log(10.0)
                     -1119669/($T*$T))) /$gamma_OH;
    $K1 = (10.0 **(-356.3094 -0.06091964*$T +21834.37/$T +126.8339 *log($T)/log(10.0)
                     -1684915/($T*$T))) *$gamma_H2CO3 /$gamma_HCO3;
    $K2 = (10.0 **(-107.8871 -0.03252849*$T + 5151.79/$T + 38.92561*log($T)/log(10.0)
                     -563713.9/($T*$T))) *$gamma_HCO3 /$gamma_CO3;
}


################################################################################
#
# Set up a subroutine so that the sorts are done numerically.
#
sub numerically { $a <=> $b; }


################################################################################
#
# POWELL is a routine implementing Powell's method for minimizing a
# multidimensional function, using conjugate direction sets and discarding
# the direction of largest decrease.
#
# This is hardcoded to minimize a 2-dimensional function.  The minimization
# will terminate when the function ceases to decrease by more than ftol.
# 
# This code is translated from FORTRAN given in "Numerical Recipes" by Press
# et al., 1989, chapter 10.
#
sub powell {
    my($alk, $Ct, $ftol) = @_;
    my($itmax, $n, @p, $fret, $j, @pt, $iter, $fp, $ibig, $del, $i);
    my(@xi, @xit, $fptt, @ptt, $t, @pass, $jj);

    $itmax = 75;                   # maximum allowed number of iterations

    $n = 1;                        # number of dimensions, minus 1

    # do the minimization in log space; remember, this is log base e
    $p[0] = $p[1] = -6;
    $p[0] = log($alk) if ($alk > 0.);
    $p[1] = log($Ct)  if ($Ct  > 0.);

    @xi = ( [1, 0], [0, 1] );      # initialize the xi array.

    $fret = &get_func(@p);
    for ($j=0; $j<=$n; $j++) {
        $pt[$j] = $p[$j];
    }

    $iter = 0;
    while ($iter++ <= $itmax) {
        $fp = $fret;
        $ibig = 0;
        $del = 0.0;
        for ($i=0; $i<=$n; $i++) {
            for ($j=0; $j<=$n; $j++) {
                $xit[$j] = $xi[$j][$i];
            }
            $fptt = $fret;

            @pass = &linmin(@p, @xit, $n);
            $fret = pop(@pass);        # get the passed args: (@p, @xit, $fret)
            for ($jj=0; $jj<=$n; $jj++) { $p[$jj] = shift(@pass); }
            @xit = @pass;

            if (abs($fptt -$fret) > $del) {
                $del = abs($fptt - $fret);
                $ibig = $i;
            }
        }
        if (2.0 * abs($fp -$fret) <= $ftol *( abs($fp) +abs($fret))) {
            $alk = exp($p[0]);
            $Ct = exp($p[1]);
            return ($alk, $Ct, $fret, $iter);
        }
        for ($j=0; $j<=$n; $j++) {
            $ptt[$j] = 2.0 * $p[$j] - $pt[$j];
            $xit[$j] = $p[$j] - $pt[$j];
            $pt[$j] = $p[$j];
        }
        $fptt = &get_func(@ptt);
        next if ($fptt >= $fp);
        $t = 2.0 *($fp -2.0 * $fret +$fptt) * ($fp -$fret -$del)**2
           - $del * ($fp - $fptt)**2;
        next if ($t >= 0.0);

        @pass = &linmin(@p, @xit, $n);
        $fret = pop(@pass);        # get the passed args: (@p, @xit, $fret)
        for ($jj=0; $jj<=$n; $jj++) { $p[$jj] = shift(@pass); }
        @xit = @pass;

        for ($j=0; $j<=$n; $j++) {
            $xi[$j][$ibig] = $xit[$j];
        }
    }

#   print "Powell exceeding maximum iterations.\n";
    $alk = exp($p[0]);
    $Ct = exp($p[1]);
    return ($alk, $Ct, $fret, $iter);
}


################################################################################
#
# LINMIN is a routine that carries out the minimization of a function along
# a given direction.
#
# This code is translated from FORTRAN given in "Numerical Recipes" by Press
# et al., 1989, chapter 10.
#
sub linmin {
    my($ax, $xx, $bx, $xmin, $fret, $tol, $j);
    my($n, @p, @xi);

    $tol = 0.0001;

    $n = pop(@_);                       # get the passed args: (@p, @xi, $n)
    for ($j=0; $j<=$n; $j++) { $p[$j] = shift(@_); }
    @xi = @_;

    for ($j=0; $j<=$n; $j++) {
        $pcom[$j] = $p[$j];             # @pcom and @xicom are globals.
        $xicom[$j] = $xi[$j];
    }
    $ax = 0.0;
    $xx = 1.0;

    ($ax, $xx, $bx) = &mnbrak($ax, $xx);             # Bracket the minimum.
    ($xmin, $fret) = &brent($ax, $xx, $bx, $tol);    # Find the minimum.

    for ($j=0; $j<=$n; $j++) {
        $xi[$j] = $xmin * $xi[$j];
        $p[$j] += $xi[$j];
    }
    return (@p, @xi, $fret);
}


################################################################################
#
# MNBRAK is a routine for initially isolating a minimum.  Given initial points
# ax and bx, the routine searches in the downhill direction and returns new
# points ax, bx, and cx which bracket a minimum of the function.
# 
# This code is translated from FORTRAN given in "Numerical Recipes" by Press
# et al., 1989, chapter 10.
#
sub mnbrak {
    my($ax, $bx) = @_;
    my($gold, $glimit, $tiny);
    my($fa, $fb, $cx, $fc, $r, $q, $u, $fu, $ulim);

    $gold = 1.618034;                 # default ratio for interval magnification
    $glimit = 100.0;                  # maximum magnification allowed
    $tiny = 10 **(-20);

    $fa = &onedim($ax);
    $fb = &onedim($bx);
    if ($fb > $fa) {
        ($ax, $bx) = ($bx, $ax);      # switcheroo!
        ($fa, $fb) = ($fb, $fa);      # want to go downhill from a to b
    }
    $cx = $bx +$gold *($bx - $ax);    # first guess for c
    $fc = &onedim($cx);

    while ($fb >= $fc) {
        $r = ($bx - $ax) *($fb - $fc);       # compute u by parabolic
        $q = ($bx - $cx) *($fb - $fa);       #  extrapolation from a,b,c
        $u = $bx - (($bx -$cx) *$q -($bx -$ax) *$r)
             /(2.0 * &sign( &max( abs($q-$r), $tiny), $q-$r));
        $ulim = $bx +$glimit *($cx - $bx);   # limit to the extrapolation
        if (($bx -$u) *($u - $cx) > 0.0) {   # parabolic u is between b and c; try it
            $fu = &onedim($u);
            if ($fu < $fc) {                 # minimum found between b and c
                return ($bx, $u, $cx);
            } elsif ($fu > $fb) {            # minimum found between a and u
                return ($ax, $bx, $u);
            }
            $u = $cx +$gold *($cx - $bx);    # parabolic fit no good.  use golden section
            $fu = &onedim($u);
        } elsif (($cx - $u) *($u - $ulim) > 0.0) {     # parabolic extrapolation ok
            $fu = &onedim($u);
            if ($fu < $fc) {                 # no minimum found, but do a
                $bx = $cx;                   #  quick golden section extrapolation
                $cx = $u;
                $u = $cx +$gold *($cx - $bx);
                $fb = $fc;
                $fc = $fu;
                $fu = &onedim($u);
            }
        } elsif (($u - $ulim) *($ulim - $cx) >= 0.0) {  # limit the extrapolation
            $u = $ulim;
            $fu = &onedim($u);
        } else {                             # reject the parabolic extrapolation
            $u = $cx +$gold *($cx - $bx);
            $fu = &onedim($u);
        }
        $ax = $bx;              # reset values for new iteration
        $bx = $cx;              # remember, it's downhill from a to b to c (to u)
        $cx = $u;
        $fa = $fb;
        $fb = $fc;
        $fc = $fu;
    }
    return ($ax, $bx, $cx);     # minimum found between a and c
}


################################################################################
#
# Subroutine BRENT is a routine that isolates the minimum of a function in
# one dimension using Brent's method.  This is translated from the FORTRAN
# code given in "Numerical Recipes" by Press et al., 1989, chapter 10.
#
# Values passed to brent are ax, bx, and cx such that bx is between ax and cx
# and f(bx) is less than both f(ax) and f(cx).  The minimization ends when the
# minimum is isolated to within a precision of $tol.  Tol should generally
# be no smaller than the square root of the computer's floating point precision.
#
sub brent {
    my($ax, $bx, $cx, $tol) = @_;
    my($itmax, $cgold, $zeps);
    my($a, $b, $v, $w, $x, $e, $fv, $fw, $fx, $iter);
    my($xm, $tol1, $tol2, $r, $q, $p, $etemp, $d, $u, $fu );

    $itmax = 100;            # maximum number of iterations
    $cgold = 0.3819660;      # golden ratio
    $zeps = 10 **(-10);      # a small number that guards against near-zero problems

    ($a, $b) = sort numerically ($ax, $cx);  # $a and $b in ascending order
    $v = $w = $x = $bx;
    $e = 0.0;                          # distance moved on step before last
    $fv = $fw = $fx = &onedim($x);

    for ($iter=1; $iter<=$itmax; $iter++) {
        $xm = 0.5 *($a + $b);
        $tol1 = $tol *abs($x) + $zeps;
        $tol2 = 2.0 *$tol1;

        if (abs($x - $xm) <= ($tol2 - 0.5 *($b - $a))) { return ($x, $fx); }

        if (abs($e) > $tol1) {         # construct a trial parabolic fit
            $r = ($x - $w) *($fx - $fv);
            $q = ($x - $v) *($fx - $fw);
            $p = ($x - $v) *$q - ($x - $w) *$r;
            $q = 2.0 *($q - $r);
            if ($q > 0.0) { $p *= -1; }
            $q = abs($q);
            $etemp = $e;
            $e = $d;
            if (abs($p) >= abs(0.5 *$q *$etemp) || $p <= $q *($a - $x)
                 || $p >= $q *($b - $x)) {
                if ($x >= $xm) {       # take the golden section step,
                    $e = $a - $x;      #  into the larger segment
                } else {
                    $e = $b - $x;
                }
                $d = $cgold * $e;
            } else {                   # take the parabolic step
                $d = $p / $q;
                $u = $x + $d;
                if (($u - $a) < $tol2 || ($b - $u) < $tol2) {
                    $d = &sign($tol1, ($xm - $x));
                }
            }
        } else {                       # take the golden section step,
            if ($x >= $xm) {           #  into the larger segment
                $e = $a - $x;
            } else {
                $e = $b - $x;
            }
            $d = $cgold * $e;
        }
        if (abs($d) >= $tol1) {        # never evaluate the function less than a
            $u = $x + $d;              #  distance tol away from a point already
        } else {                       #  evaluated
            $u = $x + &sign($tol1, $d);
        }
        $fu = &onedim($u);
        if ($fu <= $fx) {              # prepare to save u as x, fu as fx
            if ($u >= $x) {            # new point a at x
                $a = $x;
            } else {                   # new point b at x
                $b = $x;
            }
            $v = $w;                   # v is the previous value of w
            $fv = $fw;
            $w = $x;                   # w is the point with the 2nd least
            $fw = $fx;                 #  function value
            $x = $u;                   # x is the point with the lowest
            $fx = $fu;                 #  function value
        } else {                       # new boundary point, but x stays the same
            if ($u < $x) {
                $a = $u;               # new point a at u
            } else {
                $b = $u;               # new point b at u
            }
            if ($fu <= $fw || $w == $x) {
                $v = $w;
                $fv = $fw;
                $w = $u;
                $fw = $fu;
            } elsif ($fu <= $fv || $v == $x || $v == $w) {
                $v = $u;
                $fv = $fu;
            }
        }
    }
#   print "Brent exceeding maximum iterations.\n";
    return ($x, $fx);
}


################################################################################
#
# Needed an analog to the Fortran SIGN function.
#
sub sign {
    my($first, $second) = @_;
    if ($second >= 0.0) {
        return abs($first);
    } else {
        return (-1.0 * abs($first));
    }
}


################################################################################
#
# Needed an analog to the Fortran MAX function.
# (probably could implement this more generally using a
#   "reverse sort numerically" and returning the first value of the list)
#
sub max {
    my($first, $second) = @_;
    if ($first >= $second) {
        return $first;
    } else {
        return $second;
    }
}


################################################################################
#
# This is a function that hides the multidimensionality of the function
# being minimized from the MNBRAK and BRENT routines, which are designed to
# operate in only one dimension.
#
# Element 0 is Alk, Element 1 is Ct.
#
sub onedim {
    my($x) = @_;
    my(@xt, $onedim);

    $xt[0] = $pcom[0] + $x * $xicom[0];  # @pcom and @xicom are globals
    $xt[1] = $pcom[1] + $x * $xicom[1];
    $onedim = &get_func(@xt);
    return $onedim;
}


################################################################################
#
# Calculate the sum of squared residuals between the measured and theoretical
# volumes of acid titrant needed to reach a measured pHs.
#
# Trick:  The optimization routines operate on Alk and Ct in log space.
#         So, I have to unlog them here before using them.
#
# Other trick:  The fit can be restricted to certain pH regions with the
#               $do_carb and $do_bicarb variables.
#
sub get_func {
    my($alk, $Ct) = @_;
    my($sum, $H, $ph, $vol);

    $alk = exp($alk);
    $Ct  = exp($Ct);
    $sum = 0.0;
    foreach $ph ( reverse sort numerically keys(%data) ) {
        next if ( $do_bicarb && $ph > $bicarb_ph_upper );  # fitting bicarbonate endpoint
        last if ( $do_bicarb && $ph < $bicarb_ph_lower );  # fitting bicarbonate endpoint
        next if ( $do_carb   && $ph > $carb_ph_upper );    # fitting carbonate endpoint
        last if ( $do_carb   && $ph < $carb_ph_lower );    # fitting carbonate endpoint
        $H = 10.0 ** (-1.0 * $ph);
        next if ($H == 0.0);
        $vol = $counts_per_mL *$volume
               /($acid_conc *$cor_factor +$Kw/$H -$H/$gamma_H)
               *($alk -$Ct*($K1*$H +2.0*$K1*$K2)/($H*$H +$K1*$H +$K1*$K2)
               -$Kw/$H +$H/$gamma_H);
        $sum += ($vol - $data{$ph}) *($vol - $data{$ph});
    }
    return $sum;
}


################################################################################
#
# Subroutine to calculate a WEIGHTED linear regression, given an array of
# (x,y) data.  The points nearer the endpoint are weighted more.
#
# After calculating the line, the last point in the array may
# be tossed out if it doesn't contribute to the linearity of the fit.
# Then, the fit is redone.  This comparing and tossing is continually
# done until a doneness criteria is satisfied.
#
sub regression {

    my(@xy) = @_;
    my($sumx, $sumy, $sumxy, $sumx2, $sumy2);
    my($n, $i, @x, @y, $wn, $j);
    my($xmean, $ymean, $slope, $intercept, $last_slope, $last_intercept);
    my($done, $endpt, $last_endpt, $r, $r2, $last_r2);

    @x = ();
    @y = ();

    $n = ($#xy + 1) / 2;
    return ($n, $n, $n) if ($n < 2);

    for ($i=0; $i<$n; $i++) {
        $x[$i] = $xy[2 * $i];
        $y[$i] = $xy[2 * $i +1];
    }

    $n++; $done = 0;
    $last_endpt = 0.0001;
    $last_r2 = 0;
    while (! $done) {
        $n--;                           # lop off the last point
        $sumx  = 0.0;
        $sumy  = 0.0;
        $sumxy = 0.0;
        $sumx2 = 0.0;
        $sumy2 = 0.0;

        $wn = 0;
        for ($i=0; $i<$n; $i++) {
            for ($j=0; $j<$n-$i; $j++) {
                $wn++;
                $sumx  += $x[$i];
                $sumy  += $y[$i];
                $sumxy += $x[$i] * $y[$i];
                $sumx2 += $x[$i] * $x[$i];
                $sumy2 += $y[$i] * $y[$i];
            }
        }

        $xmean = $sumx / $wn;
        $ymean = $sumy / $wn;

        $slope = ($wn * $sumxy - $sumx * $sumy) /($wn * $sumx2 - $sumx * $sumx);
        $intercept = $ymean - $slope * $xmean;
        $endpt = -1.0 * $intercept / $slope;

        $r = ($sumxy - $sumx *$sumy /$wn)
            / sqrt(($sumx2 - $sumx *$sumx /$wn) *($sumy2 - $sumy *$sumy /$wn));
        $r2 = $r * $r;

        if ($r2 < $last_r2) {
            return ($n+1, $last_slope, $last_intercept);

        } elsif ($r2 - $last_r2 < 0.01 &&
                 abs($endpt - $last_endpt)/$last_endpt < 0.01) {
            return ($n, $slope, $intercept);
        }
        $done = 1 if ($n == 2);

        $last_r2 = $r2;
        $last_endpt = $endpt;
        $last_slope = $slope;
        $last_intercept = $intercept;
    }

    return ($n, $slope, $intercept);
}


################################################################################
#
# Calculate criteria used in determining presence of non-carbonate alkalinity.
#
sub get_criteria {
    my ($alk, $bicarb_endpt_vol, $carb_endpt_vol, $endpt_fixed) = @_;
    my ($check_vol, $check_str, $check_str1, $check_str2, $add_str);
    my ($H, $H0, $ph, $vol, $mean_err, $n, $carb_vol_err);

#
#   Calculate the fit of the data to the theoretical carbonate titration curve.
#   An r-squared is useless.  Similarly, the sign test and the Wald-Wolfowitz
#   Runs Test don't provide the information I want.  There's no goodness-of-fit
#   statistic that is perfect, so I'll use a lax criteria on the mean
#   absolute error.
#
#   Calculate the theoretical volumes and the mean absolute error.
#
    $H0 = 10.0 ** (-1.0 * $highest_ph);
    $n = 0;
    $mean_err = 0;
    foreach $ph ( reverse sort numerically keys(%data) ) {
        $H = 10.0 ** (-1.0 * $ph);
        next if ($H == 0.0);
        $vol = $counts_per_mL *$volume
               /($acid_conc *$cor_factor +$Kw/$H -$H/$gamma_H)
               *($alk -$Kw/$H +$H/$gamma_H -($alk -$Kw/$H0 +$H0/$gamma_H)
               *(($K1*$H +2.0*$K1*$K2) /($H*$H +$K1*$H +$K1*$K2))
               *(($H0*$H0 +$K1*$H0 +$K1*$K2) /($K1*$H0 +2.0*$K1*$K2)));
        $n++;
        $mean_err += abs($data{$ph} - $vol);
    }
    $mean_err /= $n;

#
#   Calculate the theoretical position of the carbonate endpoint.
#
    $check_vol = $counts_per_mL *$volume / ($acid_conc *$cor_factor)
                 *($alk -($alk -$Kw/$H0 +$H0/$gamma_H)
                 *($H0*$H0 +$K1*$H0 +$K1*$K2) /($K1*$H0 +2.0*$K1*$K2));
    $carb_vol_err = abs($carb_endpt_vol - $check_vol);

#
#   Set some feedback strings.
#
    $check_str = $check_str1 = $check_str2 = "";
    if ($highest_ph > 8.3 && $carb_endpt_vol > 0 &&
           $carb_vol_err > 0.05 * $bicarb_endpt_vol &&
           $carb_vol_err * $F1 / $volume > 1.0) {
        $check_str1 = sprintf("The carbonate endpoint found in this titration
\($vol_fmt $vol_string\) does not agree well with the calculated theoretical
carbonate endpoint for this sample \($vol_fmt $vol_string\). ",
$carb_endpt_vol, $check_vol);
    }

    if ($n > 10 && $mean_err > 0.05 * $bicarb_endpt_vol
          && $mean_err * $F3 / $volume > 1.0) {
        if ($check_str1) {
            $check_str2 = "In addition, the ";
        } else {
            $check_str2 = "The ";
        }
        $check_str2 .= sprintf("theoretical carbonate titration curve
\(as specified by an alkalinity of $meq_fmt meq/L and a sample pH of
$ph_fmt\) does not fit your data well, giving a mean absolute titrant volume
error of $vol_fmt $vol_string. ", $alk *1000., $highest_ph, $mean_err);
    }

    if ($check_str1 && ! $check_str2) {
        $check_str1 .= "This is an indication that something significant,
other than hydroxide, carbonate, and bicarbonate, was neutralized in this
titration. ";
        if ($endpt_fixed && $carb_fixed) {
            $check_str1 .= sprintf("Alternatively, the fixed endpoint you
specified for the carbonate endpoint \(pH $ph_fmt\) is in error. ",
$carb_endpt2);
        }

    } elsif (! $check_str1 && $check_str2) {
        $check_str2 .= "This is an indication that something significant,
other than hydroxide, carbonate, and bicarbonate, was neutralized in this
titration. ";
        if ($endpt_fixed && $bicarb_fixed) {
            $check_str2 .= sprintf("Alternatively, the fixed endpoint you
specified for the bicarbonate endpoint \(pH $ph_fmt\) is in error. ",
$bicarb_endpt2);
        }

    } elsif ($check_str1 && $check_str2) {
        $check_str2 .= "This is an indication that something significant,
other than hydroxide, carbonate, and bicarbonate, was neutralized in this
titration. ";
        if ($endpt_fixed) {
            if ($carb_fixed && $bicarb_fixed) {
                $check_str2 .= sprintf("Alternatively, at least one of
the fixed endpoints you specified \(pH $ph_fmt and $ph_fmt\) are in
error. ", $carb_endpt2, $bicarb_endpt2);

            } elsif ($carb_fixed) {
                $check_str2 .= sprintf("Alternatively, the fixed endpoint
you specified for the carbonate endpoint \(pH $ph_fmt\) is in error. ",
$carb_endpt2);

            } elsif ($bicarb_fixed) {
                $check_str2 .= sprintf("Alternatively, the fixed endpoint
you specified for the bicarbonate endpoint \(pH $ph_fmt\) is in error. ",
$bicarb_endpt2);
            }
        }
    }

    if ($check_str1 || $check_str2) {
#       $add_str = "";
#       if ($userinhouse) {
            $add_str = "<strong>Use the \&quot\;e\&quot\; remark code when
entering the carbonate and bicarbonate concentrations into NWIS.</strong>";
#       }
        $check_str = "<strong><span class=red>Warning:</span></strong> "
                   . $check_str1 . $check_str2 . "
<strong>The calculated values for carbonate and bicarbonate may not represent
their true concentrations in the sample and should be reported only as
estimates.</strong> " . $add_str . "
[<a href=\"criteria.html\">more info</a>]";
    }

    return ($check_str);
}


################################################################################
#
# Calculate the theoretical carbonate speciation.
#
# Alk in eq/L.
# Return values are in mg/L.
#
sub get_speciation {
    my ($alk) = @_;
    my ($H0, $carb, $bicarb, $oh);

    $H0     = 10.0 ** (-1.0 * $highest_ph);
    $bicarb = $bicarb_meq *($alk -$Kw/$H0 +$H0/$gamma_H) /(1.0 +2.0*$K2/$H0);
    $carb   = $carb_meq   *($alk -$Kw/$H0 +$H0/$gamma_H) /(2.0 +$H0/$K2);
    $oh     = $oh_meq * $Kw / $H0;

    $bicarb = 0 if ($bicarb < 0);
    $carb   = 0 if ($carb   < 0);
    $oh     = 0 if ($oh     < 0);

    return ($bicarb, $carb, $oh);
}


################################################################################
#
# Set the specifics of a graph axis.
#
sub set_axis_specs {
    my ($datamin, $datamax) = @_;
    my ($range, $order, $min, $max, $major, $minorticks, $prec);

    $min = $max = $prec = 0;
    $range = ($datamax - $datamin) *10000;
    $order = -4;
    while ($range/10. > 1) {
        $range /= 10.;
        $order++;
    }
    if ($range >= 5) {
        $major = 10**$order;
        $minorticks = 4;
        $prec = abs($order) if ($order < 0);
    } elsif ($range >= 2) {
        $major = 5 * (10**($order-1));
        $minorticks = 4;
        $prec = abs($order-1) if ($order < 1);
    } else {
        $major = 2 * (10**($order-1));
        $minorticks = 3;
        $prec = abs($order-1) if ($order < 1);
    }
    until ($min + $major > $datamin) { $min += $major; }
    until ($max > $datamax) { $max += $major; }

    return { min => $min, max => $max, major => $major,
             minorticks => $minorticks, prec => $prec };
}
1;
