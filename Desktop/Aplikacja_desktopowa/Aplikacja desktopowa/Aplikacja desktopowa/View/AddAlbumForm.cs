using System;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class AddAlbumForm : Form
    {
        public string AlbumName => textBoxName.Text.Trim();
        public string AlbumDescription => textBoxDescription.Text.Trim();

        private TextBox textBoxName;
        private TextBox textBoxDescription;
        private Button buttonAdd;
        private Button buttonCancel;

        public AddAlbumForm()
        {
            Text = "Dodaj nowy album";
            Width = 350;
            Height = 210;
            StartPosition = FormStartPosition.CenterParent;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;

            var labelName = new Label { Text = "Nazwa albumu:", Left = 20, Top = 20, Width = 100 };
            textBoxName = new TextBox { Left = 130, Top = 18, Width = 180 };

            var labelDesc = new Label { Text = "Opis albumu:", Left = 20, Top = 60, Width = 100 };
            textBoxDescription = new TextBox { Left = 130, Top = 58, Width = 180 };

            buttonAdd = new Button { Text = "Dodaj", Left = 130, Width = 80, Top = 110, DialogResult = DialogResult.OK };
            buttonCancel = new Button { Text = "Anuluj", Left = 230, Width = 80, Top = 110, DialogResult = DialogResult.Cancel };

            Controls.Add(labelName);
            Controls.Add(textBoxName);
            Controls.Add(labelDesc);
            Controls.Add(textBoxDescription);
            Controls.Add(buttonAdd);
            Controls.Add(buttonCancel);

            AcceptButton = buttonAdd;
            CancelButton = buttonCancel;
        }
    }
}
