"""
Rebuilds the skpop_redesign.scad geometry as real BREP solids (via CadQuery /
OpenCascade) and exports STEP files. STEP is the format Inventor imports
natively (File > Open > select .step) as an accurate solid body - unlike STL,
which is just a mesh.

Same parameters as skpop_redesign.scad - keep both files in sync if you tweak
dimensions.
"""
import cadquery as cq
import os

OUT = "/home/claude/step_out"
os.makedirs(OUT, exist_ok=True)

# ---------- Card + clearance ----------
card_h, card_w = 120, 70
card_clear_l, card_clear_w = 5, 4
stack_depth = 48

tray_wall = 2.5
tray_L = card_h + card_clear_l          # 125
tray_W = card_w + card_clear_w          # 74
tray_H = stack_depth                    # 48
tray_outer_L = tray_L + 2*tray_wall
tray_outer_W = tray_W + 2*tray_wall
tray_outer_H = tray_H + tray_wall

dispense_slot_h = 6
dispense_slot_w = tray_W - 16

# ---------- Mechanism bay (case) ----------
case_wall = 3
mech_clear = 3
mech_headroom = 56
mech_in_L = tray_outer_L + 2*mech_clear
mech_in_W = tray_outer_W + 2*mech_clear
mech_in_H = tray_outer_H + mech_headroom

shaft_height = mech_in_H - 20   # placeholder - measure on the real assembly
shaft_dia = 6

front_slot_h = dispense_slot_h + 2
front_slot_w = dispense_slot_w

# ---------- Arduino Uno R3 ----------
uno_L, uno_W = 68.6, 53.4
uno_clear = 6
uno_bay_L = uno_L + 2*uno_clear
uno_bay_W = uno_W + 2*uno_clear
uno_bay_H = 25
uno_standoff_d = 6
uno_standoff_h = 6
uno_hole_d = 3.2
uno_holes = [(15.24, 2.54), (15.24, 50.80), (66.04, 17.78), (66.04, 45.72)]

# ---------- YM2758 servo ----------
servo_body_L = 23
servo_body_W = 12.4
servo_body_H = 29.5
servo_tab_span = 32.6
servo_tab_hole_d = 2.2
servo_pocket_clear = 1.0
case_wall2 = 3


def rounded_shell(l, w, h, r, wall, open_top=True):
    outer = cq.Workplane("XY").rect(l, w, centered=False).extrude(h)
    outer = outer.edges("|Z").fillet(r)
    inner_r = max(r - wall, 0.1)
    inner_h = h + 2 if open_top else h - 2 * wall
    inner_z = wall
    inner = (cq.Workplane("XY").workplane(offset=inner_z)
             .rect(l - 2*wall, w - 2*wall, centered=False)
             .transformed(offset=(wall, wall, 0))
             .extrude(inner_h))
    inner = inner.edges("|Z").fillet(inner_r)
    return outer.cut(inner)


def rounded_solid(l, w, h, r):
    return cq.Workplane("XY").rect(l, w, centered=False).extrude(h).edges("|Z").fillet(r)


def card_tray():
    part = rounded_shell(tray_outer_L, tray_outer_W, tray_outer_H, 4, tray_wall, open_top=True)
    slot = (cq.Workplane("XY").workplane(offset=tray_wall)
            .transformed(offset=(-1, (tray_outer_W - dispense_slot_w)/2, 0))
            .rect(tray_wall + 2, dispense_slot_w, centered=False)
            .extrude(dispense_slot_h))
    return part.cut(slot)


def main_case():
    outer_L = mech_in_L + 2*case_wall
    outer_W = mech_in_W + 2*case_wall
    outer_H = mech_in_H + case_wall

    part = rounded_shell(outer_L, outer_W, outer_H, 5, case_wall, open_top=True)

    slot = (cq.Workplane("XY").workplane(offset=case_wall + tray_wall)
            .transformed(offset=(-1, (outer_W - front_slot_w)/2, 0))
            .rect(case_wall + 2, front_slot_w, centered=False)
            .extrude(front_slot_h))
    part = part.cut(slot)

    shaft = (cq.Workplane("YZ").workplane(offset=outer_L - case_wall - 1)
             .transformed(offset=(-(outer_W/2 - 15), shaft_height - 15))
             .circle(shaft_dia/2)
             .extrude(case_wall + 2))
    part = part.cut(shaft)

    rail1 = (cq.Workplane("XY").workplane(offset=case_wall)
             .transformed(offset=(case_wall, case_wall + 1, 0))
             .rect(mech_in_L - 2, 2, centered=False).extrude(4))
    rail2 = (cq.Workplane("XY").workplane(offset=case_wall)
             .transformed(offset=(case_wall, outer_W - case_wall - 3, 0))
             .rect(mech_in_L - 2, 2, centered=False).extrude(4))
    part = part.union(rail1).union(rail2)
    return part


def electronics_bay():
    outer_L = uno_bay_L + servo_body_W + 3*case_wall2
    outer_W = max(uno_bay_W, servo_tab_span + 4) + 2*case_wall2
    outer_H = uno_bay_H + case_wall2

    part = rounded_shell(outer_L, outer_W, outer_H, 4, case_wall2, open_top=True)

    usb_slot = (cq.Workplane("YZ").workplane(offset=-1)
                .transformed(offset=(-(outer_W/2 + 16), case_wall2 + 2))
                .rect(32, 14, centered=False)
                .extrude(case_wall2 + 2))
    part = part.cut(usb_slot)

    shaft = (cq.Workplane("YZ").workplane(offset=-1)
             .transformed(offset=(-(outer_W/2), shaft_height - 15 - case_wall2))
             .circle(shaft_dia/2)
             .extrude(case_wall2 + 2))
    part = part.cut(shaft)

    # Uno standoffs
    for (px, py) in uno_holes:
        x = case_wall2 + uno_clear + px
        y = case_wall2 + uno_clear + py
        standoff = (cq.Workplane("XY").workplane(offset=case_wall2)
                    .transformed(offset=(x, y, 0))
                    .circle(uno_standoff_d/2).extrude(uno_standoff_h))
        hole = (cq.Workplane("XY").workplane(offset=case_wall2 - 1)
                .transformed(offset=(x, y, 0))
                .circle(uno_hole_d/2).extrude(uno_standoff_h + 2))
        part = part.union(standoff).cut(hole)

    # Servo pocket (friction-fit) cut into the floor area
    servo_x = uno_bay_L + 2*case_wall2
    pocket = (cq.Workplane("XY").workplane(offset=case_wall2)
              .transformed(offset=(servo_x, (outer_W - servo_body_W)/2, 0))
              .rect(servo_body_L + servo_pocket_clear, servo_body_W + servo_pocket_clear, centered=False)
              .extrude(servo_body_H))
    part = part.cut(pocket)

    # Tab screw holes through the floor
    for dx in (-servo_tab_span/2, servo_tab_span/2):
        x = servo_x + servo_body_L/2 + dx
        y = outer_W/2
        hole = (cq.Workplane("XY").workplane(offset=-1)
                .transformed(offset=(x, y, 0))
                .circle(servo_tab_hole_d/2).extrude(case_wall2 + 2))
        part = part.cut(hole)

    return part


def case_lid():
    outer_L = mech_in_L + 2*case_wall
    outer_W = mech_in_W + 2*case_wall
    base = rounded_solid(outer_L, outer_W, 4, 5)
    pocket = (cq.Workplane("XY").workplane(offset=-1)
              .transformed(offset=(case_wall + 1, case_wall + 1, 0))
              .rect(outer_L - 2*case_wall - 2, outer_W - 2*case_wall - 2, centered=False)
              .extrude(6))
    lip = (cq.Workplane("XY").workplane(offset=-6)
           .transformed(offset=(case_wall + 1.5, case_wall + 1.5, 0))
           .rect(outer_L - 2*case_wall - 3, outer_W - 2*case_wall - 3, centered=False)
           .extrude(6))
    return base.cut(pocket).union(lip)


def electronics_lid():
    outer_L = uno_bay_L + servo_body_W + 3*case_wall2
    outer_W = max(uno_bay_W, servo_tab_span + 4) + 2*case_wall2
    return rounded_solid(outer_L, outer_W, 3, 4)


parts = {
    "card_tray_resized": card_tray,
    "case_resized": main_case,
    "electronics_bay_arduino_servo": electronics_bay,
    "case_lid_resized": case_lid,
    "electronics_bay_lid": electronics_lid,
}

for name, fn in parts.items():
    print("building", name)
    shape = fn()
    path = os.path.join(OUT, name + ".step")
    cq.exporters.export(shape, path)
    print("  ->", path)
