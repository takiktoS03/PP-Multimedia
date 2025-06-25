using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using GMap.NET;
using GMap.NET.MapProviders;
using GMap.NET.WindowsForms;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Net.Http;
using System.Windows.Forms;
using System.Text.Json;

namespace Aplikacja_desktopowa.View
{
    public class PhotoEditForm : Form
    {
        public PhotoMetadata Photo { get; private set; }
        public bool RemoveFromAlbum { get; private set; } = false;

        private PhotoService photoService = new PhotoService();

        private TextBox textBoxTitle;
        private TextBox textBoxDescription;
        private ComboBox comboBoxLocation;
        private TextBox textBoxTags;
        private Button buttonSave;
        private Button buttonRemove;
        private Button buttonDownload;

        private GMapControl gmap;
        private Label labelMapError;

        private PictureBox pictureBox;

        // Example locations (replace with your own or load dynamically)
        private readonly List<string> availableLocations = new List<string>
        {
            "Paris",
            "Tokyo"
        };

        public PhotoEditForm(PhotoMetadata photo)
        {
            Photo = photo;

            GMap.NET.GMaps.Instance.Mode = GMap.NET.AccessMode.ServerOnly;

            textBoxTitle = new TextBox { Text = photo.Title, Left = 20, Top = 20, Width = 300 };
            textBoxDescription = new TextBox { Text = photo.Description, Left = 20, Top = 60, Width = 300 };
            comboBoxLocation = new ComboBox
            {
                Left = 20,
                Top = 100,
                Width = 300,
                DropDownStyle = ComboBoxStyle.DropDownList
            };
            comboBoxLocation.Items.AddRange(availableLocations.ToArray());
            if (!string.IsNullOrWhiteSpace(photo.Location) && comboBoxLocation.Items.Contains(photo.Location))
                comboBoxLocation.SelectedItem = photo.Location;
            else
                comboBoxLocation.SelectedIndex = 0;

            textBoxTags = new TextBox { Text = string.Join(",", photo.Tags ?? new List<string>()), Left = 20, Top = 140, Width = 300 };

            buttonSave = new Button { Text = "Save", Left = 20, Top = 180, Width = 120 };
            buttonRemove = new Button { Text = "Remove", Left = 140, Top = 180, Width = 120 };
            buttonDownload = new Button { Text = "Download Photo", Left = 260, Top = 180, Width = 120 };

            pictureBox = new PictureBox
            {
                Left = 370,
                Top = 20,
                Width = 500,
                Height = 500,
                SizeMode = PictureBoxSizeMode.Zoom,
                BorderStyle = BorderStyle.FixedSingle
            };
            Controls.Add(pictureBox);

            gmap = new GMapControl
            {
                Left = 370,
                Top = 530,
                Width = 500,
                Height = 500,
                MapProvider = GMapProviders.OpenStreetMap,
                MinZoom = 4,
                MaxZoom = 7,
                Zoom = 4,
                ShowCenter = false
            };
            Controls.Add(gmap);

            labelMapError = new Label
            {
                Left = 370,
                Top = 530,
                Width = 500,
                Height = 30,
                Text = "Location cannot be displayed on the map.",
                ForeColor = Color.Red,
                Visible = false
            };
            Controls.Add(labelMapError);

            if (!string.IsNullOrWhiteSpace(photo.FilePath))
            {
                try
                {
                    pictureBox.Load(photo.FilePath);
                }
                catch { }
            }

            UpdateMap(comboBoxLocation.SelectedItem?.ToString());

            comboBoxLocation.SelectedIndexChanged += (s, e) =>
            {
                UpdateMap(comboBoxLocation.SelectedItem?.ToString());
            };

            buttonSave.Click += (s, e) =>
            {
                Photo.Title = textBoxTitle.Text;
                Photo.Description = textBoxDescription.Text;
                Photo.Location = comboBoxLocation.SelectedItem?.ToString();
                Photo.Tags = new List<string>((textBoxTags.Text ?? "").Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries));
                DialogResult = DialogResult.OK;
                Close();
            };

            buttonRemove.Click += (s, e) =>
            {
                RemoveFromAlbum = true;
                DialogResult = DialogResult.OK;
                Close();
            };

            buttonDownload.Click += async (s, e) =>
            {
                try
                {
                    if (string.IsNullOrWhiteSpace(Photo.FilePath))
                    {
                        MessageBox.Show("No file path (photo URL).", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }

                    string localFileName = $"{Photo.Title}.jpg";
                    string localFilePath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyPictures), localFileName);

                    using (HttpClient client = new HttpClient())
                    {
                        var response = await client.GetAsync(Photo.FilePath);
                        response.EnsureSuccessStatusCode();

                        using (var fs = new FileStream(localFilePath, FileMode.Create, FileAccess.Write))
                        {
                            await response.Content.CopyToAsync(fs);
                        }
                    }

                    MessageBox.Show($"Photo downloaded to: {localFilePath}", "Success", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
                catch (Exception ex)
                {
                    MessageBox.Show($"Error downloading photo: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            };

            Controls.Add(textBoxTitle);
            Controls.Add(textBoxDescription);
            Controls.Add(comboBoxLocation);
            Controls.Add(textBoxTags);
            Controls.Add(buttonSave);
            Controls.Add(buttonRemove);
            Controls.Add(buttonDownload);

            Text = "Edit Photo";
            ClientSize = new System.Drawing.Size(1500, 1000);
            FormBorderStyle = FormBorderStyle.FixedDialog;
            StartPosition = FormStartPosition.CenterParent;
        }

        private void UpdateMap(string location)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(location))
                {
                    gmap.Visible = false;
                    labelMapError.Text = "No location selected.";
                    labelMapError.Visible = true;
                    return;
                }

                if (TryParseCoordinates(location, out double lat, out double lng))
                {
                    if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180)
                    {
                        gmap.Visible = true;
                        labelMapError.Visible = false;
                        gmap.Position = new PointLatLng(lat, lng);
                        gmap.Zoom = 4; 
                        return;
                    }
                }
                else if (location.Length >= 3 && ContainsLetter(location))
                {
                    var point = GeocodeAddress(location);
                    if (point != null)
                    {
                        gmap.Visible = true;
                        labelMapError.Visible = false;
                        gmap.Position = point.Value;
                        gmap.Zoom = 4;
                        return;
                    }
                }

                gmap.Visible = false;
                labelMapError.Text = "Location cannot be displayed on the map.";
                labelMapError.Visible = true;
            }
            catch (Exception ex)
            {
                gmap.Visible = false;
                labelMapError.Text = $"Map error: {ex.Message}";
                labelMapError.Visible = true;
            }
        }

        private bool TryParseCoordinates(string location, out double lat, out double lng)
        {
            lat = lng = 0;
            var parts = location.Split(',');
            if (parts.Length != 2) return false;
            return double.TryParse(parts[0].Trim(), out lat) && double.TryParse(parts[1].Trim(), out lng);
        }

        private bool ContainsLetter(string input)
        {
            foreach (char c in input)
                if (char.IsLetter(c))
                    return true;
            return false;
        }

        private PointLatLng? GeocodeAddress(string address)
        {
            try
            {
                string url = $"https://nominatim.openstreetmap.org/search?format=json&q={Uri.EscapeDataString(address)}";
                using (var client = new System.Net.WebClient())
                {
                    client.Headers.Add("User-Agent", "YourAppName");
                    string json = client.DownloadString(url);
                    var results = System.Text.Json.JsonDocument.Parse(json).RootElement;
                    if (results.GetArrayLength() > 0)
                    {
                        var first = results[0];
                        double lat = double.Parse(first.GetProperty("lat").GetString(), System.Globalization.CultureInfo.InvariantCulture);
                        double lon = double.Parse(first.GetProperty("lon").GetString(), System.Globalization.CultureInfo.InvariantCulture);
                        return new PointLatLng(lat, lon);
                    }
                }
            }
            catch { }
            return null;
        }

        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(PhotoEditForm));
            this.SuspendLayout();
            // 
            // PhotoEditForm
            // 
            this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
            this.ClientSize = new System.Drawing.Size(1230, 610);
            this.Name = "PhotoEditForm";
            this.ResumeLayout(false);

        }
    }
}