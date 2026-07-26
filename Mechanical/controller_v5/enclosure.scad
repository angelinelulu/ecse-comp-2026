// ============================================================
// Joy-Con style enclosure v3
// Full redesign at larger dimensions -- no longer edits the
// original mesh, since scaling that organic shape ~1.8x in width
// while keeping length fixed would badly distort it. This is a
// clean two-part box: front panel + back shell, hollow inside,
// no middle frame.
// ============================================================

$fn = 64;

// ---------- Overall envelope ----------
length = 100;   // X -- long axis, holds joystick + 4 buttons
width  = 60;    // Y
depth  = 50;    // Z -- total closed thickness
wall   = 3;     // wall thickness, all faces
corner_r = 24;  // outer corner rounding -- pushed much higher so the
                // footprint reads as a rounded/pill shape, not a
                // rectangle with clipped corners

lid_thickness = 3;      // front panel thickness
shell_depth   = depth - lid_thickness; // back shell's own depth

// ---------- Joystick opening ----------
joy_w = 18.5;
joy_h = 13.5;

// ---------- Buttons ----------
button_d = 8;
button_spacing = 10; // distance from cluster center to each button

// ---------- Assembly screws (corner bosses) ----------
screw_boss_d = 7;
screw_hole_d = 2.6;   // pilot hole for a self-tapping M3
screw_inset = 10;     // distance of boss center from each corner

module rounded_rect(l, w, r) {
    hull() {
        for (x = [r, l - r])
            for (y = [r, w - r])
                translate([x, y]) circle(r = r);
    }
}

// A fully rounded box (like a bar of soap) -- rounds the vertical
// corners AND the top/bottom edges, instead of just extruding a flat
// 2D outline. This is what actually makes it read as "a controller"
// rather than a rectangular block with rounded corners.
module rounded_box_3d(l, w, h, r, bevel) {
    minkowski() {
        translate([bevel, bevel, bevel])
            linear_extrude(height = h - 2*bevel)
                rounded_rect(l - 2*bevel, w - 2*bevel, max(r - bevel, 0.5));
        sphere(r = bevel, $fn = 24);
    }
}

bevel_r = 16; // outer edge rounding radius -- pushed much higher this
              // time so it's actually visible, not just a hint of rounding

module back_shell() {
    difference() {
        rounded_box_3d(length, width, shell_depth, corner_r, bevel_r);
        translate([wall, wall, wall])
            linear_extrude(height = shell_depth)
                rounded_rect(length - 2*wall, width - 2*wall, max(corner_r - wall, 1));
    }
    // corner screw bosses (solid pillars, pilot hole added in final module)
    for (pos = [[screw_inset, screw_inset], [length-screw_inset, screw_inset],
                [screw_inset, width-screw_inset], [length-screw_inset, width-screw_inset]]) {
        translate([pos[0], pos[1], 0])
            cylinder(d = screw_boss_d, h = shell_depth - 2);
    }
}

module back_shell_final() {
    boss_h = shell_depth - 2;
    pilot_depth = boss_h * 0.8; // stops inside the boss -- does NOT pierce the floor
    difference() {
        back_shell();
        for (pos = [[screw_inset, screw_inset], [length-screw_inset, screw_inset],
                    [screw_inset, width-screw_inset], [length-screw_inset, width-screw_inset]]) {
            translate([pos[0], pos[1], boss_h - pilot_depth])
                cylinder(d = screw_hole_d, h = pilot_depth + 1);
        }
    }
}

// joy_cx / button_cx let us mirror the layout for left vs right
module front_panel(joy_cx, button_cx, cy = 30) {
    difference() {
        linear_extrude(height = lid_thickness)
            rounded_rect(length, width, corner_r);

        // joystick opening
        translate([joy_cx, cy, -1])
            linear_extrude(height = lid_thickness + 2)
                square([joy_w, joy_h], center = true);

        // 4 buttons, cross pattern
        for (offset = [[0, button_spacing], [0, -button_spacing],
                        [button_spacing, 0], [-button_spacing, 0]]) {
            translate([button_cx + offset[0], cy + offset[1], -1])
                cylinder(d = button_d, h = lid_thickness + 2);
        }

        // corner screw clearance holes (match the back shell's bosses)
        for (pos = [[screw_inset, screw_inset], [length-screw_inset, screw_inset],
                    [screw_inset, width-screw_inset], [length-screw_inset, width-screw_inset]]) {
            translate([pos[0], pos[1], -1])
                cylinder(d = screw_hole_d, h = lid_thickness + 2);
        }
    }
}

// ---------- Left / Right variants ----------
// Left: joystick at the "25" end, buttons at the "75" end
module front_panel_left()  { front_panel(joy_cx = 25, button_cx = 75); }
// Right: mirrored -- joystick and buttons swap ends
module front_panel_right() { front_panel(joy_cx = 75, button_cx = 25); }

module back_shell_left()  { back_shell_final(); }
module back_shell_right() { back_shell_final(); } // back shell has no L/R-specific features
