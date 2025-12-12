import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import partnerApi from '../../api/partnerApi';

export default function EditListing() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    price: '',
    area: '',
    address: '',
    postType: 'NORMAL'
  });
  const [images, setImages] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetchingPost, setFetchingPost] = useState(true);

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 5) {
      alert('Chỉ được tải lên tối đa 5 ảnh!');
      return;
    }
    
    setImages(files);
    const previews = files.map(file => URL.createObjectURL(file));
    setImagePreviews(previews);
  };

  const removeImage = (index) => {
    const newImages = images.filter((_, i) => i !== index);
    const newPreviews = imagePreviews.filter((_, i) => i !== index);
    setImages(newImages);
    setImagePreviews(newPreviews);
    URL.revokeObjectURL(imagePreviews[index]);
  };

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const post = await partnerApi.getPostById(id);
      setFormData({
        title: post.title,
        description: post.description,
        price: post.price.toString(),
        area: post.area.toString(),
        address: post.address,
        postType: post.postType
      });
      
      // Load existing images
      if (post.imageUrls && post.imageUrls.length > 0) {
        setExistingImages(post.imageUrls);
      }
    } catch (error) {
      console.error(error);
      alert('Không tìm thấy tin đăng hoặc bạn không có quyền chỉnh sửa');
      navigate('/partner/my-listings');
    } finally {
      setFetchingPost(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
        const payload = {
          title: formData.title,
          description: formData.description,
          price: parseFloat(formData.price),
          area: parseFloat(formData.area),
          address: formData.address,
          postType: formData.postType
        };
        
        await partnerApi.updatePost(id, payload, images);
        alert('Cập nhật tin thành công! Vui lòng chờ duyệt lại.');
        
        // Clean up preview URLs
        imagePreviews.forEach(url => URL.revokeObjectURL(url));
        
        navigate('/partner/my-listings');
    } catch (error) {
        console.error(error);
        alert('Lỗi: ' + (error.response?.data?.message || 'Không thể cập nhật tin'));
    } finally {
        setLoading(false);
    }
  };

  if (fetchingPost) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-500">Đang tải...</div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-extrabold text-gray-800">Chỉnh sửa tin đăng</h1>
        <p className="text-gray-500 mt-2">Cập nhật thông tin tin đăng của bạn</p>
      </div>

      <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-lg p-8 space-y-6">
        {/* Thông tin cơ bản */}
        <div className="space-y-4">
            <h3 className="font-bold text-gray-700 border-b pb-2">1. Thông tin cơ bản</h3>
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Tiêu đề tin đăng</label>
                <input required type="text" className="w-full border-gray-300 rounded-lg p-2.5" 
                    placeholder="VD: Cho thuê phòng trọ giá rẻ gần ĐH Công Nghệ..."
                    value={formData.title}
                    onChange={e => setFormData({...formData, title: e.target.value})} />
            </div>
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Giá thuê (VNĐ/tháng)</label>
                    <input required type="number" className="w-full border-gray-300 rounded-lg p-2.5" 
                        placeholder="2000000"
                        value={formData.price}
                        onChange={e => setFormData({...formData, price: e.target.value})} />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Diện tích (m²)</label>
                    <input required type="number" className="w-full border-gray-300 rounded-lg p-2.5" 
                        placeholder="25"
                        value={formData.area}
                        onChange={e => setFormData({...formData, area: e.target.value})} />
                </div>
            </div>
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                <input required type="text" className="w-full border-gray-300 rounded-lg p-2.5" 
                    placeholder="123 Đường ABC, Quận XYZ, TP HCM"
                    value={formData.address}
                    onChange={e => setFormData({...formData, address: e.target.value})} />
            </div>
        </div>

        {/* Mô tả */}
        <div className="space-y-4">
            <h3 className="font-bold text-gray-700 border-b pb-2">2. Mô tả chi tiết</h3>
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả chi tiết</label>
                <textarea required rows="5" className="w-full border-gray-300 rounded-lg p-2.5" 
                    placeholder="Mô tả đầy đủ về phòng trọ..."
                    value={formData.description}
                    onChange={e => setFormData({...formData, description: e.target.value})}></textarea>
            </div>
        </div>

        {/* Hình ảnh */}
        <div className="space-y-4">
            <h3 className="font-bold text-gray-700 border-b pb-2">3. Hình ảnh</h3>
            
            {/* Existing images */}
            {existingImages.length > 0 && (
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Ảnh hiện tại</label>
                    <div className="grid grid-cols-5 gap-2 mb-4">
                        {existingImages.map((url, index) => (
                            <div key={index} className="relative">
                                <img 
                                    src={`http://localhost:8080${url}`} 
                                    alt={`Existing ${index + 1}`} 
                                    className="w-full h-24 object-cover rounded-lg border"
                                    onError={(e) => { e.target.src = '/placeholder-image.png'; }}
                                />
                                {index === 0 && (
                                    <span className="absolute bottom-1 left-1 bg-indigo-600 text-white text-xs px-2 py-0.5 rounded">Đại diện</span>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            )}
            
            {/* Upload new images */}
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                    {existingImages.length > 0 ? 'Thay thế bằng ảnh mới (Tối đa 5)' : 'Tải lên ảnh (Tối đa 5)'}
                </label>
                <input 
                    type="file" 
                    multiple 
                    accept="image/*" 
                    onChange={handleImageChange}
                    className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100" 
                />
                <p className="text-xs text-gray-500 mt-1">
                    {existingImages.length > 0 
                        ? 'Nếu tải ảnh mới, tất cả ảnh cũ sẽ bị thay thế. Ảnh đầu tiên sẽ là ảnh đại diện.' 
                        : 'Ảnh đầu tiên sẽ là ảnh đại diện'}
                </p>
            </div>
            
            {/* New image previews */}
            {imagePreviews.length > 0 && (
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Ảnh mới sẽ tải lên</label>
                    <div className="grid grid-cols-5 gap-2">
                        {imagePreviews.map((preview, index) => (
                            <div key={index} className="relative group">
                                <img src={preview} alt={`Preview ${index + 1}`} className="w-full h-24 object-cover rounded-lg border" />
                                <button 
                                    type="button"
                                    onClick={() => removeImage(index)}
                                    className="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                                >
                                    ×
                                </button>
                                {index === 0 && (
                                    <span className="absolute bottom-1 left-1 bg-indigo-600 text-white text-xs px-2 py-0.5 rounded">Đại diện</span>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>

        {/* Chọn gói tin */}
        <div className="space-y-4">
            <h3 className="font-bold text-gray-700 border-b pb-2">4. Loại tin đăng</h3>
            <div className="grid grid-cols-2 gap-4">
                <label className={`border rounded-xl p-4 cursor-pointer hover:border-indigo-500 ${formData.postType === 'NORMAL' ? 'border-indigo-500 bg-indigo-50' : 'border-gray-200'}`}>
                    <input type="radio" name="pkg" className="hidden" checked={formData.postType === 'NORMAL'} onChange={() => setFormData({...formData, postType: 'NORMAL'})} />
                    <div className="font-bold text-gray-900">Tin Thường</div>
                    <div className="text-xs text-gray-500">Miễn phí, hiển thị sau tin ưu tiên</div>
                </label>
                <label className={`border rounded-xl p-4 cursor-pointer hover:border-indigo-500 ${formData.postType === 'PRIORITY' ? 'border-indigo-500 bg-indigo-50' : 'border-gray-200'}`}>
                    <input type="radio" name="pkg" className="hidden" checked={formData.postType === 'PRIORITY'} onChange={() => setFormData({...formData, postType: 'PRIORITY'})} />
                    <div className="font-bold text-indigo-600">Tin Ưu tiên</div>
                    <div className="text-xs text-gray-500">Phí dịch vụ, hiển thị đầu trang</div>
                </label>
            </div>
        </div>

        <div className="pt-4 flex gap-4">
            <button type="button" onClick={() => navigate('/partner/my-listings')} className="flex-1 py-3 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition">
                Hủy bỏ
            </button>
            <button type="submit" disabled={loading} className="flex-1 py-3 bg-indigo-600 text-white font-bold rounded-xl hover:bg-indigo-700 shadow-lg transition-transform active:scale-95 disabled:opacity-50">
                {loading ? 'Đang xử lý...' : 'Cập nhật tin'}
            </button>
        </div>
      </form>
    </div>
  );
}
