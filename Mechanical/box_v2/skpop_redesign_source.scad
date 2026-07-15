// ============================================================
// SKPOP card dispenser — resized for 120 x 70 mm cards
// + Arduino Uno R3 / YM2758 servo electronics bay
// ============================================================
// All dimensions in mm. Set PART below (via -D PART="...") to
// render just one part per STL export.

// ---------- Card + clearance ----------
card_h        = 120;   // card "height" -> tray length direction
card_w        = 70;    // card "width"  -> tray width direction
card_clear_l  = 5;     // extra length clearance
card_clear_w  = 4;     // extra width clearance
stack_depth   = 48;    // capacity for stacked cards (unchanged from original)

tray_wall     = 2.5;
tray_L        = card_h + card_clear_l;      // 125
tray_W        = card_w + card_clear_w;      // 74
tray_H        = stack_depth;                // 48

tray_outer_L  = tray_L + 2*tray_wall;
tray_outer_W  = tray_W + 2*tray_wall;
tray_outer_H  = tray_H + tray_wall;         // closed bottom, open top

dispense_slot_h = 6;     // height of feed slot at front-bottom of tray
dispense_slot_w = tray_W - 16;

// ---------- Mechanism bay (case) ----------
case_wall      = 3;
mech_clear     = 3;                 // clearance around tray so it can drop in/slide
mech_headroom  = 56;                // space above tray for roller/gear/knob/servo drive
mech_in_L      = tray_outer_L + 2*mech_clear;
mech_in_W      = tray_outer_W + 2*mech_clear;
mech_in_H      = tray_outer_H + mech_headroom;

// Height (from case floor) of the drive shaft that the gear train/knob
// turns on. MEASURE THIS on your actual assembled mechanism and update it —
// it is not something the STL bounding boxes could tell us.
shaft_height   = mech_in_H - 20;     // placeholder guess, adjust before printing
shaft_dia      = 6;                  // clearance hole for coupler shaft

// Front dispensing slot cut through the case front wall, aligned with tray's
front_slot_h   = dispense_slot_h + 2;   // a little extra clearance
front_slot_w   = dispense_slot_w;

// ---------- Arduino Uno R3 ----------
uno_L = 68.6;
uno_W = 53.4;
uno_clear = 6;
uno_bay_L = uno_L + 2*uno_clear;   // ~80.6
uno_bay_W = uno_W + 2*uno_clear;   // ~65.4
uno_bay_H = 25;                    // clearance for USB/DC jack + header pins + wires
uno_standoff_d = 6;
uno_standoff_h = 6;                // lifts board off floor for pins/solder joints
uno_hole_d = 3.2;
// Official Arduino Uno R3 mounting hole coordinates (mm from board corner)
uno_holes = [ [15.24, 2.54], [15.24, 50.80], [66.04, 17.78], [66.04, 45.72] ];

// ---------- YM2758 (SG90-class) servo ----------
servo_body_L = 23;      // along output-shaft axis
servo_body_W = 12.4;
servo_body_H = 29.5;
servo_tab_span = 32.6;  // outer edge to outer edge across mounting tabs
servo_tab_hole_d = 2.2;
servo_pocket_clear = 1.0; // snug friction fit

case_wall2 = 3;

// ============================================================
module rounded_box(l, w, h, r, shell=false, wall=0, open_top=false){
    // basic rounded-rect prism; if shell, hollow it out leaving `wall` thickness
    module solid(ll, ww, hh, rr){
        hull(){
            for(x=[rr, ll-rr]) for(y=[rr, ww-rr])
                translate([x,y,0]) cylinder(h=hh, r=rr, $fn=32);
        }
    }
    if(!shell){
        solid(l,w,h,r);
    } else {
        difference(){
            solid(l,w,h,r);
            translate([wall,wall, open_top ? wall : -1])
                solid(l-2*wall, w-2*wall, open_top ? h : h-2*wall, max(r-wall,0.1));
        }
    }
}

// ---------- 1) CARD TRAY ----------
module card_tray(){
    difference(){
        rounded_box(tray_outer_L, tray_outer_W, tray_outer_H, 4,
                    shell=true, wall=tray_wall, open_top=true);
        // dispensing slot at front-bottom (front = x near 0)
        translate([-1, (tray_outer_W-dispense_slot_w)/2, tray_wall])
            cube([tray_wall+2, dispense_slot_w, dispense_slot_h]);
    }
}

// ---------- 2) MAIN CASE (mechanism bay) ----------
module main_case(){
    outer_L = mech_in_L + 2*case_wall;
    outer_W = mech_in_W + 2*case_wall;
    outer_H = mech_in_H + case_wall; // closed bottom, open top for lid

    difference(){
        rounded_box(outer_L, outer_W, outer_H, 5,
                    shell=true, wall=case_wall, open_top=true);

        // front dispensing slot through case front wall, matching tray slot height
        translate([-1, (outer_W-front_slot_w)/2, case_wall + tray_wall])
            cube([case_wall+2, front_slot_w, front_slot_h]);

        // rear cutout to bolt the electronics bay on + pass the drive shaft through
        translate([outer_L-case_wall-1, outer_W/2-15, shaft_height-15])
            rotate([0,90,0])
            cylinder(h=case_wall+2, d=shaft_dia, $fn=24);
    }

    // tray guide rails on the floor
    for(side=[case_wall+1, outer_W-case_wall-1-2])
        translate([case_wall, side, case_wall])
            cube([mech_in_L-2, 2, 4]);
}

// ---------- 3) ELECTRONICS BAY (Arduino + servo, bolts to rear of case) ----------
module electronics_bay(){
    outer_L = uno_bay_L + servo_body_W + 3*case_wall2; // uno bay + servo pocket side-by-side
    outer_W = max(uno_bay_W, servo_tab_span+4) + 2*case_wall2;
    outer_H = uno_bay_H + case_wall2;

    difference(){
        rounded_box(outer_L, outer_W, outer_H, 4,
                    shell=true, wall=case_wall2, open_top=true);

        // USB + barrel jack access slot on the outer short wall (x=0 side)
        translate([-1, outer_W/2-16, case_wall2+2])
            cube([case_wall2+2, 32, 14]);

        // shaft pass-through aligned with main_case's hole (mate the two parts here)
        translate([-1, outer_W/2, shaft_height-15-case_wall2])
            rotate([0,90,0])
            cylinder(h=case_wall2+2, d=shaft_dia, $fn=24);
    }

    // Arduino Uno standoffs
    translate([case_wall2+uno_clear, case_wall2+uno_clear, case_wall2])
        for(p = uno_holes)
            translate([p[0], p[1], 0])
                difference(){
                    cylinder(h=uno_standoff_h, d=uno_standoff_d, $fn=24);
                    cylinder(h=uno_standoff_h+1, d=uno_hole_d, $fn=16);
                }

    // Servo pocket (friction-fit slot) alongside the Uno bay
    servo_x = uno_bay_L + 2*case_wall2;
    translate([servo_x, (outer_W-servo_body_W)/2, case_wall2])
        difference(){
            cube([servo_body_L+servo_pocket_clear, servo_body_W+servo_pocket_clear, servo_body_H]);
            translate([-1,-1,-1]) cube([1,1,1]); // no-op placeholder to keep diff non-trivial
        }
    // tab screw holes for the servo (through the pocket floor)
    translate([servo_x+servo_body_L/2, (outer_W)/2, 0])
        for(dx=[-servo_tab_span/2, servo_tab_span/2])
            translate([dx,0,0])
                cylinder(h=case_wall2+2, d=servo_tab_hole_d, $fn=16);
}

// ---------- 4) LIDS ----------
module case_lid(){
    outer_L = mech_in_L + 2*case_wall;
    outer_W = mech_in_W + 2*case_wall;
    difference(){
        rounded_box(outer_L, outer_W, 4, 5);
        translate([case_wall+1, case_wall+1, -1])
            rounded_box(outer_L-2*case_wall-2, outer_W-2*case_wall-2, 6, 3);
    }
    // lip that drops into the case opening
    translate([case_wall+1.5, case_wall+1.5, -6])
        rounded_box(outer_L-2*case_wall-3, outer_W-2*case_wall-3, 6, 3);
}

module electronics_lid(){
    outer_L = uno_bay_L + servo_body_W + 3*case_wall2;
    outer_W = max(uno_bay_W, servo_tab_span+4) + 2*case_wall2;
    rounded_box(outer_L, outer_W, 3, 4);
}

// ============================================================
PART = "card_tray"; // overridden via -D PART="..." on the command line

if(PART=="card_tray") card_tray();
else if(PART=="main_case") main_case();
else if(PART=="electronics_bay") electronics_bay();
else if(PART=="case_lid") case_lid();
else if(PART=="electronics_lid") electronics_lid();
