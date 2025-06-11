using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using Aplikacja_desktopowa.Model;

namespace Aplikacja_desktopowa.View
{
    public class PhotoEditForm : Form
    {
        public PhotoMetadata Photo { get; private set; }
        public bool RemoveFromAlbum { get; private set; } = false;

        private TextBox textBoxTitle;
        private TextBox textBoxDescription;
        private TextBox textBoxLocation;
        private TextBox textBoxTags;
        private Button buttonSave;
        private Button buttonRemove;

        public PhotoEditForm(PhotoMetadata photo)
        {
            Photo = photo;

            textBoxTitle = new TextBox { Text = photo.Title, Left = 20, Top = 20, Width = 300 };
            textBoxDescription = new TextBox { Text = photo.Description, Left = 20, Top = 60, Width = 300 };
            textBoxLocation = new TextBox { Text = photo.Location, Left = 20, Top = 100, Width = 300 };
            textBoxTags = new TextBox { Text = string.Join(",", photo.Tags ?? new List<string>()), Left = 20, Top = 140, Width = 300 };

            buttonSave = new Button { Text = "Zapisz", Left = 20, Top = 180, Width = 100 };
            buttonRemove = new Button { Text = "Usuñ z albumu", Left = 140, Top = 180, Width = 120 };

            buttonSave.Click += (s, e) =>
            {
                Photo.Title = textBoxTitle.Text;
                Photo.Description = textBoxDescription.Text;
                Photo.Location = textBoxLocation.Text;
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

            Controls.Add(textBoxTitle);
            Controls.Add(textBoxDescription);
            Controls.Add(textBoxLocation);
            Controls.Add(textBoxTags);
            Controls.Add(buttonSave);
            Controls.Add(buttonRemove);

            Text = "Edycja zdjêcia";
            ClientSize = new System.Drawing.Size(360, 230);
            FormBorderStyle = FormBorderStyle.FixedDialog;
            StartPosition = FormStartPosition.CenterParent;
        }
    }
}
