I. Hướng dẫn dùng Admob Module để hiển thị quảng cáo.

1. Hiện tại ở project base đã import sẵn module Admob, nếu dự án lấy từ base thì có thể chuyển sang bước 2.
- Import folder Admod như 1 module vào prọect: File > New > Import Module...

2. Khơi tạo Admob SDK khi App được khởi tạo:
  // Initialize Admob SDK
        AdMobSdk.initializeSdk(this) {
            Log.d(TAG, "Admob SDK initialized!!")
        }
        
3. Hiển thị Quảng cáo theo loại (Banner, Native, Interstitial)
- Banner:
  Ví dụ khi cần hiển thị banner
  BannerView.Builder(context, /* container -> truyền vào 1 view được layout để hiển thị quảng cáo. (có thể dùng FrameLayout) */)
      .setAdUnitId()  // required
      .setBgColor()   // optional
      .setListener()  // optional
      .build()
      
- Interstitial:
  Khi cần hiển thị quảng cáo loại Interstitial
  + Khởi tạo để load quảng cáo
    val adsView = InterstitialView.Builder(this)
        .setAdUnitId("")  // required
        .setListener()    // optional -> có thể cần phải lắng nghe callback để biết trạng thái của việc load quảng caó để biết quảng cáo đã sẵn sàng cho                                việc hiển thị.
        .build()
  + Hiển thị quảng cáo
    adsView.showAd()
  + Nếu cần pre-load quảng cáo cho lần hiển thị tiếp theo gọi hàm:
    fun reloadAd()
    
- Native:
  a, class "NativeView"
  Khi cần hiển thị quảng cáo Native, khởi tạo tương tự như 2 thằng ở trên.
  + Cần 1 file layout xml của custom layout (được cung cấp hoặc theo tài liệu thiết kế) -> truyền vào param: "layoutId" của Builder.
  + Ở màn hình cần hiển thị, cân xác định view (vị trí, kích thước) để chưa quảng cáo cần hiển thị. -> setContainer()
  Ví dụ:
          NativeView.Builder(this, /*File layout*/)
            .setAdUnitId("")  // required
            .setBgColor(1)  // optional
            .setListener()  // optional
            .setContainer(FrameLayout(this)) // required
            .setTitleTextViewId(1)      // required -> cần set để hiển thị cho đúng
            .setBodyTextViewId(1)       // required -> cần set để hiển thị cho đúng
            .setAdvertiserTextViewId(1) // required -> cần set để hiển thị cho đúng
            .setIconImageViewId(1)      // required -> cần set để hiển thị cho đúng
            .setMediaViewId(1)          // required -> cần set để hiển thị cho đúng
            .setOptionViewId(1)         // required -> cần set để hiển thị cho đúng
            .setCtaButtonId(1)          // required -> cần set để hiển thị cho đúng
            .build()

  b, class "NativeViewPreload" -> Dùng cho trường hợp cần load quảng cáo trước mà không xác định được container (setContainer()) tại lúc này -> ở thời điểm hiển thị mới biết được container. Ví dụ show popup, dialog..
  + Chỉ cần gọi constructor để init và gọi fun showAds(viewContainer: FrameLayout) khi cần hiển thị quảng cáo.
  + Param: "viewContainer: FrameLayout" -> truyền vào FrameLayout là layout được thiết kế để hiển thị quảng cáo.

Done!!!

II. Hướng dẫn config In-App Purchase

- Cần public key của app tương ứng (lấy từ tài khoản developer) -> thay public tương ứng cho biến "const val PUBLIC_KEY" trong class "InAppPurchase"
- Thêm dependency vào file gradle:

<------
  implementation "com.android.billingclient:billing-ktx:4.0.0"
  
----->

- Sử dựng các hàm đã có sẵn trong class "InAppPurchase" để xử lý mua hàng theo logic nghiệp vụ của từng ứng dựng
