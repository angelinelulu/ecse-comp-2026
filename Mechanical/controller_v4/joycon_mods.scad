// ============================================================
// Joy-Con shell modifications
// Base meshes were split out of the original Joycon_L_R_V2.STL
// (front/middle/back shells x left/right) and are CSG-edited here.
// All the numbers below came from measuring the original STL by
// ray-casting it in Python -- double check against your real
// board/parts before printing, especially the ESP32-S3 cavity.
// ============================================================

$fn = 64;

// ---------- Front shell: circle -> rectangle ----------
rect_w = 20;
rect_h = 20;

// ---------- Front shell: one square -> two squares ----------
square_size = 5;   // each new square, mm
square_gap  = 2;   // gap between the two squares, mm
plug_margin = 1;   // extra margin around old hole when plugging, mm

// ---------- Back shell: ESP32-S3-DevKitC-1 cavity ----------
devkit_w      = 26;   // board width + clearance (DevKitC-1 is ~25.5mm)
devkit_l      = 62;   // pocket length (board is ~69mm; DevKit hangs slightly
                       // past one end into the USB slot cavity rather than
                       // needing the pocket to reach all the way to the
                       // shell's rounded ends, which are too thin to cut into)
devkit_depth  = 10;   // pocket depth cut in from the inner face
usb_slot_w    = 8;     // USB-C access slot width
usb_slot_h    = 3;    // USB-C access slot height
wire_hole_d   = 3;    // wire pass-through hole diameter
wire_holes_per_side = 6;

// A box tall enough to guarantee a clean cut through local shell
// thickness regardless of surface curvature.
module cut_box(cx, cy, z_lo, z_hi, w, h) {
    translate([cx - w/2, cy - h/2, z_lo])
        cube([w, h, z_hi - z_lo]);
}

// Enlarges an existing round hole into a rectangle. Since the
// rectangle fully contains the old circle, we can just subtract
// the rectangle directly -- no need to plug the old hole first.
module circle_to_rect(body_file, cx, cy, z_lo, z_hi, w, h) {
    difference() {
        import(body_file);
        cut_box(cx, cy, z_lo, z_hi, w, h);
    }
}

// Plugs the old square hole, then cuts two smaller squares with a
// solid rib between them.
module square_to_two_squares(cx, cy, z_lo, z_hi, old_w, old_h) {
    plug_w = max(old_w, square_size * 2 + square_gap) + plug_margin * 2;
    plug_h = max(old_h, square_size) + plug_margin * 2;

    union() {
        cut_box(cx, cy, z_lo, z_hi, plug_w, plug_h); // plug (added as solid)
    }
}

module cut_two_squares(cx, cy, z_lo, z_hi) {
    offset = square_size/2 + square_gap/2;
    cut_box(cx - offset, cy, z_lo, z_hi, square_size, square_size);
    cut_box(cx + offset, cy, z_lo, z_hi, square_size, square_size);
}

// ---------- FRONT LEFT ----------
module front_left_modified() {
    old_sq_w = 6.2; old_sq_h = 6.5;
    old_sq_cx = 28.2; old_sq_cy = 23.2;
    circ_cx = 75.0; circ_cy = 17.7;
    z_lo = -2; z_hi = 13;

    difference() {
        union() {
            difference() {
                import("front_left.stl");
                cut_box(circ_cx, circ_cy, z_lo, z_hi, rect_w, rect_h);
            }
            // plug old square hole
            square_to_two_squares(old_sq_cx, old_sq_cy, z_lo, z_hi, old_sq_w, old_sq_h);
        }
        cut_two_squares(old_sq_cx, old_sq_cy, z_lo, z_hi);
    }
}

// ---------- FRONT RIGHT ----------
module front_right_modified() {
    old_sq_w = 5.4; old_sq_h = 5.7;
    old_sq_cx = 90.4; old_sq_cy = 58.7;
    circ_cx = 47.1; circ_cy = 68.6;
    z_lo = -2; z_hi = 13;

    difference() {
        union() {
            difference() {
                import("front_right.stl");
                cut_box(circ_cx, circ_cy, z_lo, z_hi, rect_w, rect_h);
            }
            square_to_two_squares(old_sq_cx, old_sq_cy, z_lo, z_hi, old_sq_w, old_sq_h);
        }
        cut_two_squares(old_sq_cx, old_sq_cy, z_lo, z_hi);
    }
}

// ---------- BACK LEFT (ESP32-S3-DevKitC-1 cavity) ----------
module back_left_modified() {
    z_lo = 61.03; z_hi = 73.89;
    pocket_z_lo = z_lo;               // cut in from inner face
    pocket_z_hi = z_lo + devkit_depth;

    cav_cx = 50; cav_cy = 16.5;

    difference() {
        import("back_left.stl");

        // main board pocket
        cut_box(cav_cx, cav_cy, pocket_z_lo, pocket_z_hi, devkit_l, devkit_w);

        // USB-C access slot, full depth, at one end of the pocket
        cut_box(cav_cx - devkit_l/2, cav_cy, z_lo - 1, z_hi + 1, usb_slot_h*2, usb_slot_w);

        // wire pass-through holes along both long edges of the pocket
        for (i = [0:wire_holes_per_side-1]) {
            x = cav_cx - devkit_l/2 + 8 + i * (devkit_l - 16)/(wire_holes_per_side-1);
            translate([x, cav_cy - devkit_w/2, pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = z_hi - pocket_z_hi + 1);
            translate([x, cav_cy + devkit_w/2, pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = z_hi - pocket_z_hi + 1);
        }
    }
}

// ---------- BACK RIGHT (ESP32-S3-DevKitC-1 cavity, mirrored) ----------
module back_right_modified() {
    z_lo = 68.4; z_hi = 81.26;
    pocket_z_lo = z_lo;
    pocket_z_hi = z_lo + devkit_depth;

    cav_cx = 50; cav_cy = 69.5;

    difference() {
        import("back_right.stl");

        cut_box(cav_cx, cav_cy, pocket_z_lo, pocket_z_hi, devkit_l, devkit_w);
        cut_box(cav_cx - devkit_l/2, cav_cy, z_lo - 1, z_hi + 1, usb_slot_h*2, usb_slot_w);

        for (i = [0:wire_holes_per_side-1]) {
            x = cav_cx - devkit_l/2 + 8 + i * (devkit_l - 16)/(wire_holes_per_side-1);
            translate([x, cav_cy - devkit_w/2, pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = z_hi - pocket_z_hi + 1);
            translate([x, cav_cy + devkit_w/2, pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = z_hi - pocket_z_hi + 1);
        }
    }
}

// ---------- Render selection ----------
// Comment/uncomment to export one part at a time (OpenSCAD exports
// whatever's actually rendered, so keep only one active for STL export).

front_left_modified();
// translate([0,0,0]) front_right_modified();
// back_left_modified();
// back_right_modified();

// ============================================================
// Battery bay: extend the back shell outward to fit a NACCON
// 18650 2600mAh cell (18.0mm dia x 65.2mm) in its holder, plus
// the ESP32-S3-DevKitC-1, stacked in Z within the same footprint
// (there isn't enough length to fit them side by side).
//
// NOTE: holder outer dimensions are ASSUMED (22 x 72 x 21mm) since
// I don't have your specific holder's exact size -- measure yours
// and adjust battery_w / battery_l / battery_h below if different.
// ============================================================

battery_w = 22;   // holder width (perpendicular to cell axis)
battery_l = 68;   // holder length (along cell axis) -- trimmed to fit
                   // the shell's straight section; verify against
                   // your actual holder before printing
battery_h = 21;   // holder height (through the depth of the shell)
back_ext  = 25;   // extra material added to the back shell's outer face
divider_t = 4;    // solid wall separating the ESP32 shelf from the
                   // battery bay below it (also where wire holes go)
outer_wall = 2;   // wall left over the battery on the new outer face

// Returns the 2D outline of a shell at a given Z height, used to
// grow the extension outward with a shape that matches the shell
// instead of a plain rectangular block.
module shell_silhouette(file, z_slice) {
    projection(cut = true)
        translate([0, 0, -z_slice])
            import(file);
}

module extend_shell(file, z_hi, ext) {
    union() {
        import(file);
        translate([0, 0, z_hi])
            linear_extrude(height = ext)
                shell_silhouette(file, z_hi - 0.05);
    }
}

module back_left_with_battery() {
    file = "back_left.stl";
    z_lo = 61.03; z_hi = 73.89;
    new_outer_z = z_hi + back_ext;

    cav_cx = 50; cav_cy = 16.5;

    esp_pocket_z_lo = z_lo;
    esp_pocket_z_hi = z_lo + devkit_depth;

    batt_pocket_z_hi = new_outer_z - outer_wall;
    batt_pocket_z_lo = batt_pocket_z_hi - battery_h;

    difference() {
        extend_shell(file, z_hi, back_ext);

        // ESP32-S3 shelf (shallow, near original inner face)
        cut_box(cav_cx, cav_cy, esp_pocket_z_lo, esp_pocket_z_hi, devkit_l, devkit_w);
        cut_box(cav_cx - devkit_l/2, cav_cy, z_lo - 1, esp_pocket_z_hi + 1, usb_slot_h*2, usb_slot_w);
        for (i = [0:wire_holes_per_side-1]) {
            x = cav_cx - devkit_l/2 + 8 + i * (devkit_l - 16)/(wire_holes_per_side-1);
            translate([x, cav_cy - devkit_w/2, esp_pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = divider_t + 0.2);
            translate([x, cav_cy + devkit_w/2, esp_pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = divider_t + 0.2);
        }

        // Battery bay (deep, near the new outer face)
        cut_box(cav_cx, cav_cy, batt_pocket_z_lo, batt_pocket_z_hi, battery_l, battery_w);
    }
}

module back_right_with_battery() {
    file = "back_right.stl";
    z_lo = 68.4; z_hi = 81.26;
    new_outer_z = z_hi + back_ext;

    cav_cx = 50; cav_cy = 69.5;

    esp_pocket_z_lo = z_lo;
    esp_pocket_z_hi = z_lo + devkit_depth;

    batt_pocket_z_hi = new_outer_z - outer_wall;
    batt_pocket_z_lo = batt_pocket_z_hi - battery_h;

    difference() {
        extend_shell(file, z_hi, back_ext);

        cut_box(cav_cx, cav_cy, esp_pocket_z_lo, esp_pocket_z_hi, devkit_l, devkit_w);
        cut_box(cav_cx - devkit_l/2, cav_cy, z_lo - 1, esp_pocket_z_hi + 1, usb_slot_h*2, usb_slot_w);
        for (i = [0:wire_holes_per_side-1]) {
            x = cav_cx - devkit_l/2 + 8 + i * (devkit_l - 16)/(wire_holes_per_side-1);
            translate([x, cav_cy - devkit_w/2, esp_pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = divider_t + 0.2);
            translate([x, cav_cy + devkit_w/2, esp_pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = divider_t + 0.2);
        }

        cut_box(cav_cx, cav_cy, batt_pocket_z_lo, batt_pocket_z_hi, battery_l, battery_w);
    }
}

// ============================================================
// v2 changes:
//  1. Remove the two-square feature entirely (plug it, no re-cut)
//     -- only the joystick rectangle + the original 4 round
//     buttons remain as openings on the front shell.
//  2. Add a local depth boss on the MIDDLE FRAME behind the
//     joystick opening, sized for the Jaycar XC4422 module
//     (45 x 27 x ~33mm, of which the stick pokes out through the
//     front panel -- only the ~18mm pot body needs to sit inside).
//  3. Add a small PCB pocket (25x25mm, 4-switch board) in the back
//     shell's shallow shelf area, alongside the ESP32-S3.
// ============================================================

joy_module_w = 45;   // XC4422 length
joy_module_h = 27;   // XC4422 width
joy_pocket_depth = 17; // extra depth added behind the front panel --
                        // kept under the ~18.7mm gap to the back shell so
                        // the boss doesn't physically collide with it
joy_wire_holes = 4;

pcb_switch_w = 25;   // 4-switch PCB, 2.5x2.5cm
pcb_switch_h = 25;
pcb_switch_depth = 6;

// ---------- FRONT LEFT v2 (rectangle only, square plugged/removed) ----------
module front_left_v2() {
    old_sq_w = 6.2; old_sq_h = 6.5;
    old_sq_cx = 28.2; old_sq_cy = 23.2;
    circ_cx = 75.0; circ_cy = 17.7;
    z_lo = -2; z_hi = 13;

    union() {
        difference() {
            import("front_left.stl");
            cut_box(circ_cx, circ_cy, z_lo, z_hi, rect_w, rect_h);
        }
        // plug the old square hole solid -- no new cut, so it just disappears
        cut_box(old_sq_cx, old_sq_cy, z_lo, z_hi, old_sq_w + plug_margin*2, old_sq_h + plug_margin*2);
    }
}

module front_right_v2() {
    old_sq_w = 5.4; old_sq_h = 5.7;
    old_sq_cx = 90.4; old_sq_cy = 58.7;
    circ_cx = 47.1; circ_cy = 68.6;
    z_lo = -2; z_hi = 13;

    union() {
        difference() {
            import("front_right.stl");
            cut_box(circ_cx, circ_cy, z_lo, z_hi, rect_w, rect_h);
        }
        cut_box(old_sq_cx, old_sq_cy, z_lo, z_hi, old_sq_w + plug_margin*2, old_sq_h + plug_margin*2);
    }
}

// ---------- MIDDLE FRAME: joystick depth boss + 4-switch PCB pocket ----------
// The 4-switch PCB sits directly behind the existing 4 round button
// holes (so the buttons can press the switches through the front
// shell) -- it fits inside the frame's own thickness, no extra
// depth needed there. The joystick module needs real extra depth,
// so it gets a boss of its own.
module middle_left_with_joystick_bay() {
    file = "middle_left.stl";
    frame_z_lo = 31.2;
    frame_z_hi = 42.34; // back-facing surface of the middle frame
    cx = 75.0; cy = 17.7;

    boss_w = joy_module_w + 6;  // clearance margin
    boss_h = joy_module_h + 6;
    boss_z_lo = frame_z_hi - 2; // 2mm overlap into the frame for a solid union
    boss_z_hi = frame_z_hi + joy_pocket_depth;

    pocket_z_lo = frame_z_hi + 0.5; // pocket itself stays clear of the frame
    pocket_z_hi = boss_z_hi - 2; // 2mm back wall retained

    // 4-switch PCB, centered on the button cluster
    pcb_cx = 47.1; pcb_cy = 17.65;
    pcb_z_lo = frame_z_lo - 0.5;
    pcb_z_hi = frame_z_lo + pcb_switch_depth;

    difference() {
        union() {
            import(file);
            cut_box(cx, cy, boss_z_lo, boss_z_hi, boss_w, boss_h); // the boss itself, added as solid
        }
        cut_box(cx, cy, pocket_z_lo, pocket_z_hi, joy_module_w, joy_module_h);

        // wire exit holes through the boss's back wall
        for (i = [0:joy_wire_holes-1]) {
            wx = cx - joy_module_w/2 + 6 + i * (joy_module_w - 12)/(joy_wire_holes-1);
            translate([wx, cy, pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = (boss_z_hi - pocket_z_hi) + 1);
        }

        // 4-switch PCB pocket, cut into the frame's existing thickness
        cut_box(pcb_cx, pcb_cy, pcb_z_lo, pcb_z_hi, pcb_switch_w, pcb_switch_h);
    }
}

module middle_right_with_joystick_bay() {
    file = "middle_right.stl";
    frame_z_lo = 38.622;
    frame_z_hi = 49.712;
    cx = 47.1; cy = 68.6;

    boss_w = joy_module_w + 6;
    boss_h = joy_module_h + 6;
    boss_z_lo = frame_z_hi - 2;
    boss_z_hi = frame_z_hi + joy_pocket_depth;

    pocket_z_lo = frame_z_hi + 0.5;
    pocket_z_hi = boss_z_hi - 2;

    pcb_cx = 75.0; pcb_cy = 68.55; // aligned with the real button holes
    pcb_z_lo = frame_z_lo + 1;
    pcb_z_hi = frame_z_lo + 1 + pcb_switch_depth;

    difference() {
        union() {
            import(file);
            cut_box(cx, cy, boss_z_lo, boss_z_hi, boss_w, boss_h);
            // reinforcement bridge: sits in the back 3mm of the frame's
            // thickness, which the PCB pocket never reaches (pocket only
            // cuts the front ~6mm), so it stays connected no matter how
            // it overlaps in X/Y with the pocket above it
            translate([76, 56, frame_z_hi - 3])
                cube([14, 26, 3]);
        }
        cut_box(cx, cy, pocket_z_lo, pocket_z_hi, joy_module_w, joy_module_h);

        for (i = [0:joy_wire_holes-1]) {
            wx = cx - joy_module_w/2 + 6 + i * (joy_module_w - 12)/(joy_wire_holes-1);
            translate([wx, cy, pocket_z_hi - 0.1])
                cylinder(d = wire_hole_d, h = (boss_z_hi - pocket_z_hi) + 1);
        }

        cut_box(pcb_cx, pcb_cy, pcb_z_lo, pcb_z_hi, pcb_switch_w, pcb_switch_h);
    }
}

// ---------- BACK SHELL v2 ----------
// Identical to the battery version from before -- the PCB moved to
// the middle frame, so nothing changes here. Kept as an alias so the
// v2 naming is consistent across all parts.
module back_left_v2() { back_left_with_battery(); }
module back_right_v2() { back_right_with_battery(); }
