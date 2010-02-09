/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved. Use is subject to license terms. 
 * 
 * This file is available and licensed under the following license:
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * Neither the name of Sun Microsystems nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * @author Sergey A. Malenkov
 */

import java.awt.Toolkit; 
import javafx.scene.Cursor; 
import javafx.scene.image.Image; 

import javafx.scene.Cursor;
import javafx.scene.CustomNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

def D = 22;
def BG = Color.WHITE;
def FG = Color.BLACK;
def FONT = 16;
def COLORS = [
  Color.RED,
  Color.ORANGE,
  Color.YELLOW,
  Color.GREEN,
  Color.LIGHTBLUE,
  Color.BLUE,
  Color.MAGENTA,
  Color.BLACK
];

var COLOR = Color.BLACK;
var SIZE = 1;

def canvas = Group { }

function undo() {
  def index = sizeof canvas.content;
  if (index > 0) {
    delete canvas.content[index - 1] from canvas.content
  }
}
class Button extends CustomNode {
  var content: String;
  override var blocksMouse = true;
  override var cursor = Cursor.HAND;
  override function create() {
    Group {
      translateY: bind canvas.scene.height - FONT - 4
      content: [
        Rectangle {
          stroke: FG
          fill: bind if (hover)  then FG  else BG
          width: 3 * FONT
          height: FONT
          arcWidth: FONT
          arcHeight: FONT
        },
        Text {
          x: FONT / 3
          y: FONT - 2
          content: bind content
          fill: bind if (hover)  then BG  else FG
          font: Font {
            size: FONT
          }
        }
      ]
    }
  }
}


Stage {
  title: "Jsxnc 绘图程序"
  scene: Scene {
    width: 600
    height: 400
    content: [
      Rectangle {
        fill: BG
        width: bind canvas.scene.width - 1
        height: bind canvas.scene.height - 1
		cursor:CustomCursor.PENCIL
        var path: Path;
        onMousePressed: function(mouse) {
          path = Path {
            stroke: COLOR
            strokeWidth: SIZE
            strokeLineCap: StrokeLineCap.ROUND
            strokeLineJoin: StrokeLineJoin.ROUND
            elements: MoveTo { x: mouse.x   y: mouse.y }
          }
          insert path into canvas.content
        }
        onMouseDragged: function(mouse) {
          insert LineTo { x: mouse.x   y: mouse.y } into path.elements
        }
        onKeyPressed: function(key) {
          if (key.controlDown and key.code == KeyCode.VK_Z) {
            undo()
          }
        }
      },
      canvas,
      for (color in COLORS) Rectangle {
        translateX: 4 + (2 + D) * indexof color
        translateY: 4
        strokeWidth: 2
        stroke: bind if (COLOR == color)  then FG  else null
        fill: color
        width: D
        height: D
        blocksMouse: true
        cursor: Cursor.HAND
        onMousePressed: function(mouse) {
          COLOR = color
        }
      },
      for (size in [1..5]) Group {
        translateX: 0.5 * D + 4 + (2 + D) * indexof size
        translateY: 1.5 * D + 8
        content: [
          Circle {
            strokeWidth: 2
            stroke: bind if (SIZE == size)  then FG  else null
            fill: BG
            radius: D / 2
            blocksMouse: true
            cursor: Cursor.HAND
            onMousePressed: function(mouse) {
              SIZE = size
            }
          },
          Line {
            def o = (D - size) / 2 - 2;
            strokeWidth: size
            stroke: bind COLOR
            strokeLineCap: StrokeLineCap.ROUND
            startX: -o
            endX:    o
          }
        ]
      },
      Button {
        translateX: 4
        content: "清除"
        onMousePressed: function(mouse) {
          delete canvas.content
        }
      },
      Button {
        translateX: 8 + 3 * FONT
        content: "撤销"
        onMousePressed: function(mouse) {
          undo()
        }
      },
      Rectangle {
        fill: null
        stroke: FG
        width: bind canvas.scene.width - 1
        height: bind canvas.scene.height - 1
      }
    ]
  }
}
