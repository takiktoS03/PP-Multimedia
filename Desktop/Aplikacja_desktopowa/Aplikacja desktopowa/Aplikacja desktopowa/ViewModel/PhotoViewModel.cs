using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using System.Collections.Generic;
using System.Drawing;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.ViewModel
{
    /// <summary>
    /// Warstwa ViewModel dla zdjęć.
    /// </summary>
    public class PhotoViewModel
    {
        private readonly PhotoService _photoService;

        /// <summary>
        /// Inicjalizuje nową instancję klasy PhotoViewModel.
        /// </summary>
        public PhotoViewModel()
        {
            _photoService = new PhotoService();
        }

        /// <summary>
        /// Zwraca listę wszystkich zdjęć.
        /// </summary>
        public async Task<List<PhotoMetadata>> GetAllPhotosAsync()
        {
            return await _photoService.GetAllPhotosAsync();
        }

        /// <summary>
        /// Wyświetla zdjęcia w podanej kontrolce.
        /// </summary>
        public void ShowPhotosOnPanel(IEnumerable<PhotoMetadata> photos, Control parent, int startY = 10)
        {
            int y = startY;
            foreach (var photo in photos)
            {
                var pictureBox = new PictureBox
                {
                    Location = new Point(10, y),
                    Size = new Size(100, 100),
                    SizeMode = PictureBoxSizeMode.Zoom
                };

                try
                {
                    pictureBox.Load(photo.FilePath);
                }
                catch
                {
                    // Wyświetl komunikat o błędzie na ekranie
                    var errorLabel = new Label
                    {
                        Text = $"Błąd: Nie można załadować pliku: {photo.FilePath}",
                        ForeColor = Color.Red,
                        Location = new Point(10, y + 40),
                        Width = 300
                    };
                    parent.Controls.Add(errorLabel);
                }

                parent.Controls.Add(pictureBox);
                y += 110;
            }
        }
    }
}
