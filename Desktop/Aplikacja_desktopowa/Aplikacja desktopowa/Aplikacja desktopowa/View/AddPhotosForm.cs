using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using Aplikacja_desktopowa.Model;

namespace Aplikacja_desktopowa.View
{
    /// <summary>
    /// Formularz umożliwiający dodanie wielu zdjęć z metadanymi.
    /// </summary>
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

        /// <summary>
        /// Zwraca wprowadzone metadane zdjęć.
        /// </summary>
        public IReadOnlyList<PhotoMetadata> PhotoMetadatas => photoMetadatas;

        /// <summary>
        /// Zwraca lokalne ścieżki wybranych plików.
        /// </summary>
        public IReadOnlyList<string> LocalFilePaths => selectedFiles;

        /// <summary>
        /// Inicjalizuje formularz dodawania zdjęć.
        /// </summary>
        public AddPhotosForm()
        {
            Text = "Dodaj zdjęcia";
            Width = 600;
            Height = 500;
            StartPosition = FormStartPosition.CenterParent;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;

            buttonSelectFiles = new Button { Text = "Wybierz obrazy...", Left = 20, Top = 20, Width = 120 };
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

            // Tytuł
            var labelTitle = new Label { Text = "Tytuł:", Left = 160, Top = 150, Width = 400 };
            Controls.Add(labelTitle);
            textBoxTitle = new TextBox { Left = 160, Top = 170, Width = 400 };
            Controls.Add(textBoxTitle);

            // Opis
            var labelDescription = new Label { Text = "Opis:", Left = 160, Top = 200, Width = 400 };
            Controls.Add(labelDescription);
            textBoxDescription = new TextBox { Left = 160, Top = 220, Width = 400 };
            Controls.Add(textBoxDescription);

            // Lokalizacja
            var labelLocation = new Label { Text = "Lokalizacja (lat,lng lub adres):", Left = 160, Top = 250, Width = 400 };
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

            // Tagi
            var labelTags = new Label { Text = "Tagi (oddzielone przecinkami):", Left = 160, Top = 300, Width = 400 };
            Controls.Add(labelTags);
            textBoxTags = new TextBox { Left = 160, Top = 320, Width = 400 };
            Controls.Add(textBoxTags);

            textBoxLocation.TextChanged += (s, e) => ValidateLocationInput();

            buttonPrev = new Button { Text = "< Poprzednie", Left = 160, Top = 360, Width = 80 };
            buttonPrev.Click += (s, e) => { SaveCurrent(); ShowPhoto(currentIndex - 1); };
            Controls.Add(buttonPrev);

            buttonNext = new Button { Text = "Następne >", Left = 250, Top = 360, Width = 80 };
            buttonNext.Click += (s, e) => { SaveCurrent(); ShowPhoto(currentIndex + 1); };
            Controls.Add(buttonNext);

            buttonOk = new Button { Text = "OK", Left = 340, Top = 360, Width = 80, DialogResult = DialogResult.OK };
            buttonOk.Click += (s, e) => { SaveCurrent(); Close(); };
            Controls.Add(buttonOk);

            buttonCancel = new Button { Text = "Anuluj", Left = 430, Top = 360, Width = 80, DialogResult = DialogResult.Cancel };
            Controls.Add(buttonCancel);

            AcceptButton = buttonOk;
            CancelButton = buttonCancel;
        }

        /// <summary>
        /// Obsługuje wybór plików obrazów przez użytkownika.
        /// </summary>
        private void ButtonSelectFiles_Click(object sender, EventArgs e)
        {
            using (var dialog = new OpenFileDialog())
            {
                dialog.Filter = "Zdjęcia (*.jpg;*.jpeg;*.png)|*.jpg;*.jpeg;*.png";
                dialog.Multiselect = true;
                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    selectedFiles = new List<string>(dialog.FileNames);
                    photoMetadatas = new List<PhotoMetadata>(new PhotoMetadata[selectedFiles.Count]);
                    ShowPhoto(0);
                }
            }
        }

        /// <summary>
        /// Wyświetla zdjęcie i jego metadane dla podanego indeksu.
        /// </summary>
        private void ShowPhoto(int index)
        {
            if (selectedFiles.Count == 0) return;
            if (index < 0 || index >= selectedFiles.Count) return;

            currentIndex = index;
            pictureBox.Image = Image.FromFile(selectedFiles[index]);
            labelCounter.Text = $"Zdjęcie {index + 1} z {selectedFiles.Count}";

            var meta = photoMetadatas[index];
            textBoxTitle.Text = meta?.Title ?? "";
            textBoxDescription.Text = meta?.Description ?? "";
            textBoxLocation.Text = meta?.Location ?? "";
            textBoxTags.Text = meta?.Tags != null ? string.Join(",", meta.Tags) : "";

            buttonPrev.Enabled = index > 0;
            buttonNext.Enabled = index < selectedFiles.Count - 1;
        }

        /// <summary>
        /// Zapisuje metadane aktualnie wyświetlanego zdjęcia.
        /// </summary>
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

        /// <summary>
        /// Waliduje wprowadzone dane lokalizacji.
        /// </summary>
        private void ValidateLocationInput()
        {
            string location = textBoxLocation.Text.Trim();
            if (string.IsNullOrWhiteSpace(location))
            {
                labelLocationValidation.Text = "Lokalizacja jest wymagana do wyświetlenia na mapie.";
                labelLocationValidation.ForeColor = Color.Red;
                return;
            }

            if (TryParseCoordinates(location, out double lat, out double lng))
            {
                if (lat < -90 || lat > 90 || lng < -180 || lng > 180)
                {
                    labelLocationValidation.Text = "Szerokość geograficzna musi być z zakresu -90 do 90, a długość z zakresu -180 do 180.";
                    labelLocationValidation.ForeColor = Color.Red;
                }
                else
                {
                    labelLocationValidation.Text = "Poprawne współrzędne.";
                    labelLocationValidation.ForeColor = Color.Green;
                }
            }
            else if (location.Length >= 3)
            {
                labelLocationValidation.Text = "Zostanie wyświetlone jako adres na mapie.";
                labelLocationValidation.ForeColor = Color.Green;
            }
            else
            {
                labelLocationValidation.Text = "Wprowadź współrzędne (lat,lng) lub poprawny adres.";
                labelLocationValidation.ForeColor = Color.Red;
            }
        }

        /// <summary>
        /// Próbuje sparsować lokalizację jako współrzędne geograficzne.
        /// </summary>
        private bool TryParseCoordinates(string location, out double lat, out double lng)
        {
            lat = lng = 0;
            var parts = location.Split(',');
            if (parts.Length != 2) return false;
            return double.TryParse(parts[0].Trim(), out lat) && double.TryParse(parts[1].Trim(), out lng);
        }
    }
}
