package moe.feng.nhentai.api;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import moe.feng.nhentai.api.common.Constants;
import moe.feng.nhentai.model.Book;

public class BookApi {

	public static final String TAG = BookApi.class.getSimpleName();

	public static Book getBook(String id) {
		String url = Constants.getBookDetailsUrl(id);

		Document doc;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Book book = new Book();

		Elements info = doc.getElementsByAttributeValue("id", "info");
		Element element = info.get(0);

		/** Get basic info */
		book.title = element.getElementsByTag("h1").get(0).text();
		try {
			book.titleJP = element.getElementsByTag("h2").get(0).text();
		} catch (Exception e) {
			Log.v(TAG, "This book hasn\'t japanese name.");
		}
		book.bookId = id;

		/** Get page count */
		String htmlSrc = element.html();
		try {
			int position = htmlSrc.indexOf("pages");
			String s = htmlSrc.substring(0, position);
			System.out.println(s);
			s = s.substring(s.lastIndexOf("<div>") + "<div>".length(), s.length()).trim();
			System.out.println(s);
			book.pageCount = Integer.getInteger(s);
		} catch (Exception e) {

		}

		/** Get gallery id and preview image url */
		Element coverDiv = doc.getElementById("cover").getElementsByTag("a").get(0);
		for (Element e : coverDiv.getElementsByTag("img")) {
			try {
				Log.i(TAG, coverDiv.html());
				String coverUrl = e.attr("src");
				Log.i(TAG, coverUrl);
				coverUrl = coverUrl.substring(0, coverUrl.lastIndexOf("/"));
				String galleryId = coverUrl.substring(coverUrl.lastIndexOf("/") + 1, coverUrl.length());
				book.galleryId = galleryId;
				book.previewImageUrl = Constants.getThumbUrl(galleryId);
				book.bigCoverImageUrl = Constants.getBigCoverUrl(galleryId);
				break;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Log.i(TAG, book.toJSONString());

		return book;
	}

}