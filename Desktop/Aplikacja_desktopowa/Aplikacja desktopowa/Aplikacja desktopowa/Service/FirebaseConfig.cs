using Google.Cloud.Firestore;
using System;
using System.IO;

namespace Aplikacja_desktopowa.Service
{
    /// <summary>
    /// Klasa konfiguracyjna do inicjalizacji połączenia z Firebase Firestore.
    /// </summary>
    public static class FirebaseConfig
    {
        /// <summary>
        /// Identyfikator projektu Firebase.
        /// </summary>
        private const string ProjectId = "image-management-cbaee";

        /// <summary>
        /// Identyfikator bazy danych Firestore.
        /// </summary>
        private const string DatabaseId = "image-db";

        /// <summary>
        /// Nazwa pliku JSON z poświadczeniami administratora Firebase.
        /// Plik musi znajdować się w katalogu głównym aplikacji.
        /// </summary>
        private const string CredentialsFile = "image-management-cbaee-firebase-adminsdk-fbsvc-534514b3a5.json";

        /// <summary>
        /// Ustawia zmienną środowiskową GOOGLE_APPLICATION_CREDENTIALS, 
        /// wskazującą na lokalizację pliku z poświadczeniami Firebase.
        /// </summary>
        public static void Init()
        {
            string path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, CredentialsFile);
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);
        }

        /// <summary>
        /// Inicjalizuje połączenie z Firestore i zwraca instancję <see cref="FirestoreDb"/>.
        /// </summary>
        /// <returns>Obiekt FirestoreDb gotowy do użycia.</returns>
        public static FirestoreDb GetFirestoreDb()
        {
            Init();
            return new FirestoreDbBuilder
            {
                ProjectId = ProjectId,
                DatabaseId = DatabaseId
            }.Build();
        }
    }
}
