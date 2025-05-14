using Aplikacja_desktopowa.Service;
using Aplikacja_desktopowa.ViewModel;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class AlbumPhotosForm : Form
    {
        private readonly string _albumId;
        private readonly AlbumService _albumService = new AlbumService();
        private readonly PhotoService _photoService = new PhotoService();

        private readonly PhotoViewModel _photoViewModel = new PhotoViewModel();


        public AlbumPhotosForm(string albumId)
        {
            _albumId = albumId;
            Text = "Zdjêcia w albumie";
            Load += AlbumPhotosForm_Load;
            AutoScroll = true;
        }

        private async void AlbumPhotosForm_Load(object sender, EventArgs e)
        {
            var photoIds = await _albumService.GetPhotoIdsForAlbumAsync(_albumId);
            var photos = await _photoService.GetPhotosByIdsAsync(photoIds);

            _photoViewModel.ShowPhotosOnPanel(photos, this);
        }
    }
}
