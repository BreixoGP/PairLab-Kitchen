from django.urls import path
from . import endpoints

urlpatterns = [

    path('auth/register/', endpoints.register),
    path('auth/login/', endpoints.login),

    path('users/<int:id>/', endpoints.user_detail),

    path('ingredients/', endpoints.ingredients_list),

    path('pairings/', endpoints.pairings_list),

]