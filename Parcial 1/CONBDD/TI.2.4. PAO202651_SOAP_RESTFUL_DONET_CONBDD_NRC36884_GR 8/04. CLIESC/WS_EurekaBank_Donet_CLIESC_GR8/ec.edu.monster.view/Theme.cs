using System;
using System.Drawing;
using System.Windows.Forms;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    /// <summary>
    /// Identidad visual compartida de la solución TI.2.4 (S8):
    /// paleta navy/teal, tarjetas redondeadas y tipografía Segoe UI.
    /// </summary>
    internal static class Theme
    {
        public static readonly System.Drawing.Color Navy = System.Drawing.Color.FromArgb(11, 31, 58);
        public static readonly System.Drawing.Color NavyLight = System.Drawing.Color.FromArgb(19, 49, 92);
        public static readonly System.Drawing.Color Teal = System.Drawing.Color.FromArgb(15, 163, 177);
        public static readonly System.Drawing.Color TealDark = System.Drawing.Color.FromArgb(12, 132, 143);
        public static readonly System.Drawing.Color Amber = System.Drawing.Color.FromArgb(242, 165, 65);
        public static readonly System.Drawing.Color Background = System.Drawing.Color.FromArgb(244, 246, 250);
        public static readonly System.Drawing.Color Surface = System.Drawing.Color.White;
        public static readonly System.Drawing.Color Text = System.Drawing.Color.FromArgb(30, 41, 59);
        public static readonly System.Drawing.Color Muted = System.Drawing.Color.FromArgb(100, 116, 139);
        public static readonly System.Drawing.Color Success = System.Drawing.Color.FromArgb(22, 163, 74);
        public static readonly System.Drawing.Color SuccessBg = System.Drawing.Color.FromArgb(232, 247, 238);
        public static readonly System.Drawing.Color Error = System.Drawing.Color.FromArgb(220, 38, 38);
        public static readonly System.Drawing.Color ErrorBg = System.Drawing.Color.FromArgb(253, 236, 236);
        public static readonly System.Drawing.Color Border = System.Drawing.Color.FromArgb(226, 232, 240);

        public static readonly Font FuenteBase = new Font("Segoe UI", 9.75F, FontStyle.Regular);
        public static readonly Font FuenteTitulo = new Font("Segoe UI", 20F, FontStyle.Bold);
        public static readonly Font FuenteSubtitulo = new Font("Segoe UI", 10F, FontStyle.Regular);
        public static readonly Font FuenteEtiqueta = new Font("Segoe UI Semibold", 9.75F, FontStyle.Bold);
        public static readonly Font FuenteMarca = new Font("Segoe UI", 18F, FontStyle.Bold);
        public static readonly Font FuenteMonto = new Font("Segoe UI", 22F, FontStyle.Bold);

        public static System.Drawing.Drawing2D.GraphicsPath Rounded( System.Drawing.Rectangle bounds, int radius )
        {
            int d = radius * 2;
            var path = new System.Drawing.Drawing2D.GraphicsPath();
            path.AddArc(bounds.X, bounds.Y, d, d, 180, 90);
            path.AddArc(bounds.Right - d, bounds.Y, d, d, 270, 90);
            path.AddArc(bounds.Right - d, bounds.Bottom - d, d, d, 0, 90);
            path.AddArc(bounds.X, bounds.Bottom - d, d, d, 90, 90);
            path.CloseFigure();
            return path;
        }
    }

    /// <summary>Tarjeta blanca con esquinas redondeadas y borde suave.</summary>
    public class CardPanel : Panel
    {
        public int Radius { get; set; }

        public CardPanel()
        {
            Radius = 12;
            SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint |
                     ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw, true);
            BackColor = Theme.Background;
            Padding = new Padding(24);
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            var rect = new System.Drawing.Rectangle(0, 0, Width - 1, Height - 1);
            using (var path = Theme.Rounded(rect, Radius))
            using (var brush = new System.Drawing.SolidBrush(Theme.Surface))
            using (var pen = new Pen(Theme.Border))
            {
                e.Graphics.FillPath(brush, path);
                e.Graphics.DrawPath(pen, path);
            }
        }
    }

    /// <summary>Botón plano redondeado (acción principal en teal).</summary>
    public class PillButton : Button
    {
        private bool hover;

        public System.Drawing.Color BaseColor { get; set; }
        public System.Drawing.Color HoverColor { get; set; }

        public PillButton()
        {
            BaseColor = Theme.Teal;
            HoverColor = Theme.TealDark;
            FlatStyle = FlatStyle.Flat;
            FlatAppearance.BorderSize = 0;
            BackColor = BaseColor;
            ForeColor = System.Drawing.Color.White;
            Font = new Font("Segoe UI Semibold", 10.5F, FontStyle.Bold);
            Cursor = Cursors.Hand;
            Height = 42;
            SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint |
                     ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw, true);
        }

        protected override void OnMouseEnter(EventArgs e)
        {
            hover = true;
            Invalidate();
            base.OnMouseEnter(e);
        }

        protected override void OnMouseLeave(EventArgs e)
        {
            hover = false;
            Invalidate();
            base.OnMouseLeave(e);
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            var rect = new System.Drawing.Rectangle(0, 0, Width - 1, Height - 1);
            using (var path = Theme.Rounded(rect, 10))
            using (var brush = new System.Drawing.SolidBrush(hover ? HoverColor : BaseColor))
            {
                e.Graphics.FillPath(brush, path);
            }

            TextRenderer.DrawText(e.Graphics, Text, Font, rect, ForeColor,
                TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter);
        }
    }

    /// <summary>Logotipo "E" de EurekaBank dibujado con vectores (sin bitmaps).</summary>
    public class LogoControl : Control
    {
        public LogoControl()
        {
            SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint |
                     ControlStyles.OptimizedDoubleBuffer | ControlStyles.ResizeRedraw, true);
            Size = new Size(44, 44);
            BackColor = System.Drawing.Color.Transparent;
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            var g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            int lado = Math.Min(Width, Height) - 1;
            var rect = new System.Drawing.Rectangle(0, 0, lado, lado);

            using (var path = Theme.Rounded(rect, lado / 4))
            using (var brush = new System.Drawing.SolidBrush(Theme.Teal))
            {
                g.FillPath(brush, path);
            }

            // Monograma "E" en blanco
            using (var lapiz = new Pen(System.Drawing.Color.White, Math.Max(2f, lado / 9f)))
            {
                float x0 = lado * 0.30f;
                float x1 = lado * 0.70f;
                float y0 = lado * 0.26f;
                float y1 = lado * 0.74f;
                float ym = lado * 0.50f;
                g.DrawLine(lapiz, x0, y0, x1, y0);
                g.DrawLine(lapiz, x0, y0, x0, y1);
                g.DrawLine(lapiz, x0, ym, x1 * 0.93f, ym);
                g.DrawLine(lapiz, x0, y1, x1, y1);
            }

            // Acento ámbar
            using (var brush = new System.Drawing.SolidBrush(Theme.Amber))
            {
                int d = Math.Max(4, lado / 7);
                g.FillEllipse(brush, lado - d - 2, lado - d - 2, d, d);
            }
        }
    }
}
