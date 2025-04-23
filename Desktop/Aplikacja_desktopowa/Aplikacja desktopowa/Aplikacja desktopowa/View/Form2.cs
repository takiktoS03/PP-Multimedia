using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public partial class Form2 : Form
    {
        public Form2()
        {
            InitializeComponent();

            this.Text = "Photo Manager";
            this.Size = new Size(800, 600);

            pictureBoxPhoto.SizeMode = PictureBoxSizeMode.Zoom;

            this.Load += Form1_Load;
        }

        private async void Form1_Load(object sender, EventArgs e)
        {
            string imageUrl = "https://firebasestorage.googleapis.com/v0/b/image-management-cbaee.firebasestorage.app/o/photos%2Ffamily%2Fholiday.jpg?alt=media&token=e5548963-8029-4c25-bf61-65014b5a6efb";

            try
            {
                using (var client = new WebClient())
                {
                    var data = await client.DownloadDataTaskAsync(imageUrl);
                    using (var ms = new MemoryStream(data))
                    {
                        pictureBoxPhoto.Image = Image.FromStream(ms);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Błąd ładowania: " + ex.Message);
            }
        }
    }
}
