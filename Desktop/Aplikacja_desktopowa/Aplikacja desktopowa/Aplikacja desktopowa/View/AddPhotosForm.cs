using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using Aplikacja_desktopowa.Model;

namespace Aplikacja_desktopowa.View
{
    public partial class AddPhotosForm : Form
    {
        private List<string> selectedFiles = new List<string>();
        private int currentIndex = 0;

        private PictureBox pictureBox;
        private TextBox textBoxTitle;
        private TextBox textBoxDescription;
        private TextBox textBoxLocation;
        private TextBox textBoxTags;
        private Button buttonPrev;
        private Button buttonNext;
        private Button buttonSelectFiles;
        private Button buttonOk;
        private Button buttonCancel;
        private Label labelCounter;
        private Label labelLocationValidation;

        private List<PhotoMetadata> photoMetadatas = new List<PhotoMetadata>();

        public IReadOnlyList<PhotoMetadata> PhotoMetadatas => photoMetadatas;
        public IReadOnlyList<string> LocalFilePaths => selectedFiles;

        public AddPhotosForm()
        {
            Text = "Add Photos";
            Width = 600;
            Height = 500;
            StartPosition = FormStartPosition.CenterParent;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;

            buttonSelectFiles = new Button { Text = "Select Images...", Left = 20, Top = 20, Width = 120 };
            buttonSelectFiles.Click += ButtonSelectFiles_Click;
            Controls.Add(buttonSelectFiles);

            pictureBox = new PictureBox
            {
                Left = 160,
                Top = 20,
                Width = 120,
                Height = 120,
                BorderStyle = BorderStyle.FixedSingle,
                SizeMode = PictureBoxSizeMode.Zoom
            };
            Controls.Add(pictureBox);

            labelCounter = new Label { Left = 300, Top = 20, Width = 200, Height = 30 };
            Controls.Add(labelCounter);

            // Title
            var labelTitle = new Label { Text = "Title:", Left = 160, Top = 150, Width = 400 };
            Controls.Add(labelTitle);
            textBoxTitle = new TextBox { Left = 160, Top = 170, Width = 400 };
            Controls.Add(textBoxTitle);

            // Description
            var labelDescription = new Label { Text = "Description:", Left = 160, Top = 200, Width = 400 };
            Controls.Add(labelDescription);
            textBoxDescription = new TextBox { Left = 160, Top = 220, Width = 400 };
            Controls.Add(textBoxDescription);

            // Location
            var labelLocation = new Label { Text = "Location (lat,lng or address):", Left = 160, Top = 250, Width = 400 };
            Controls.Add(labelLocation);
            textBoxLocation = new TextBox { Left = 160, Top = 270, Width = 400 };
            Controls.Add(textBoxLocation);

            labelLocationValidation = new Label
            {
                Left = 160,
                Top = 295,
                Width = 400,
                ForeColor = Color.Red,
                Text = ""
            };
            Controls.Add(labelLocationValidation);

            // Tags
            var labelTags = new Label { Text = "Tags (comma separated):", Left = 160, Top = 300, Width = 400 };
            Controls.Add(labelTags);
            textBoxTags = new TextBox { Left = 160, Top = 320, Width = 400 };
            Controls.Add(textBoxTags);

            textBoxLocation.TextChanged += (s, e) => ValidateLocationInput();

            buttonPrev = new Button { Text = "< Prev", Left = 160, Top = 360, Width = 80 };
            buttonPrev.Click += (s, e) => { SaveCurrent(); ShowPhoto(currentIndex - 1); };
            Controls.Add(buttonPrev);

            buttonNext = new Button { Text = "Next >", Left = 250, Top = 360, Width = 80 };
            buttonNext.Click += (s, e) => { SaveCurrent(); ShowPhoto(currentIndex + 1); };
            Controls.Add(buttonNext);

            buttonOk = new Button { Text = "OK", Left = 340, Top = 360, Width = 80, DialogResult = DialogResult.OK };
            buttonOk.Click += (s, e) => { SaveCurrent(); Close(); };
            Controls.Add(buttonOk);

            buttonCancel = new Button { Text = "Cancel", Left = 430, Top = 360, Width = 80, DialogResult = DialogResult.Cancel };
            Controls.Add(buttonCancel);

            AcceptButton = buttonOk;
            CancelButton = buttonCancel;
        }

        private void ButtonSelectFiles_Click(object sender, EventArgs e)
        {
            using (var dialog = new OpenFileDialog())
            {
                dialog.Filter = "Images (*.jpg;*.jpeg;*.png)|*.jpg;*.jpeg;*.png";
                dialog.Multiselect = true;
                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    selectedFiles = new List<string>(dialog.FileNames);
                    photoMetadatas = new List<PhotoMetadata>(new PhotoMetadata[selectedFiles.Count]);
                    ShowPhoto(0);
                }
            }
        }

        private void ShowPhoto(int index)
        {
            if (selectedFiles.Count == 0) return;
            if (index < 0 || index >= selectedFiles.Count) return;

            currentIndex = index;
            pictureBox.Image = Image.FromFile(selectedFiles[index]);
            labelCounter.Text = $"Photo {index + 1} of {selectedFiles.Count}";

            var meta = photoMetadatas[index];
            textBoxTitle.Text = meta?.Title ?? "";
            textBoxDescription.Text = meta?.Description ?? "";
            textBoxLocation.Text = meta?.Location ?? "";
            textBoxTags.Text = meta?.Tags != null ? string.Join(",", meta.Tags) : "";

            buttonPrev.Enabled = index > 0;
            buttonNext.Enabled = index < selectedFiles.Count - 1;
        }

        private void SaveCurrent()
        {
            if (selectedFiles.Count == 0) return;
            var meta = new PhotoMetadata
            {
                Title = textBoxTitle.Text,
                Description = textBoxDescription.Text,
                Location = textBoxLocation.Text,
                Tags = new List<string>((textBoxTags.Text ?? "").Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries)),
                UploadedAt = DateTime.UtcNow
            };
            photoMetadatas[currentIndex] = meta;
        }

        // Validation logic
        private void ValidateLocationInput()
        {
            string location = textBoxLocation.Text.Trim();
            if (string.IsNullOrWhiteSpace(location))
            {
                labelLocationValidation.Text = "Location is required for map display.";
                labelLocationValidation.ForeColor = Color.Red;
                return;
            }

            if (TryParseCoordinates(location, out double lat, out double lng))
            {
                if (lat < -90 || lat > 90 || lng < -180 || lng > 180)
                {
                    labelLocationValidation.Text = "Latitude must be -90 to 90 and longitude -180 to 180.";
                    labelLocationValidation.ForeColor = Color.Red;
                }
                else
                {
                    labelLocationValidation.Text = "Valid coordinates.";
                    labelLocationValidation.ForeColor = Color.Green;
                }
            }
            else if (location.Length >= 3)
            {
                labelLocationValidation.Text = "Will be shown as address on the map.";
                labelLocationValidation.ForeColor = Color.Green;
            }
            else
            {
                labelLocationValidation.Text = "Enter coordinates (lat,lng) or a valid address.";
                labelLocationValidation.ForeColor = Color.Red;
            }
        }

        // Helper for coordinates
        private bool TryParseCoordinates(string location, out double lat, out double lng)
        {
            lat = lng = 0;
            var parts = location.Split(',');
            if (parts.Length != 2) return false;
            return double.TryParse(parts[0].Trim(), out lat) && double.TryParse(parts[1].Trim(), out lng);
        }
    }
}