using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Google.Cloud.Firestore;

[FirestoreData]
public class PhotoMetadata
{
    [FirestoreProperty]
    public string FileName { get; set; }

    [FirestoreProperty]
    public double Latitude { get; set; }

    [FirestoreProperty]
    public double Longitude { get; set; }

    [FirestoreProperty]
    public DateTime DateTaken { get; set; }
}
